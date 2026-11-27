#!/usr/bin/env python3
"""Генерирует типы из contracts/*.schema.json в Kotlin и Python.

    python3 tools/gen_contracts.py          # записать
    python3 tools/gen_contracts.py --check  # только проверить, что сгенерированное актуально
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "contracts"
KT_OUT = ROOT / "app/shared/contracts_gen/com/npcknows/bubblecast/contracts"
PY_OUT = ROOT / "server/src/shared/contracts_gen"
KT_PACKAGE = "com.npcknows.bubblecast.contracts"
HEADER = "СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/{src}. Руками не править."


def load_schemas() -> dict[str, dict]:
    """Имя файла без .schema.json -> $defs."""
    return {p.name.removesuffix(".schema.json"): json.loads(p.read_text())["$defs"]
            for p in sorted(SRC.glob("*.schema.json"))}


def ref_target(ref: str, current: str) -> tuple[str, str]:
    """'common.schema.json#/$defs/Lang' -> ('common', 'Lang')."""
    file_part, _, path = ref.partition("#")
    module = file_part.removesuffix(".schema.json") if file_part else current
    return module, path.rsplit("/", 1)[-1]


def camel(name: str) -> str:
    head, *rest = name.split("_")
    return head + "".join(w.capitalize() for w in rest)


# ---------- Kotlin ----------

def kt_type(prop: dict, current: str) -> str:
    if "$ref" in prop:
        return ref_target(prop["$ref"], current)[1]
    t = prop["type"]
    if t == "array":
        return f"List<{kt_type(prop['items'], current)}>"
    return {"string": "String", "integer": "Int", "number": "Double", "boolean": "Boolean"}[t]


def kt_doc(d: dict, indent: str = "") -> list[str]:
    return [f"{indent}/** {d['description']} */"] if "description" in d else []


def kt_file(module: str, defs: dict) -> str:
    out = [f"// {HEADER.format(src=module + '.schema.json')}", f"package {KT_PACKAGE}", "",
           "import kotlinx.serialization.SerialName", "import kotlinx.serialization.Serializable", ""]
    for name, d in defs.items():
        out += kt_doc(d)
        if "enum" in d:
            values = ", ".join(f'@SerialName("{v}") {v.upper()}' for v in d["enum"])
            out += ["@Serializable", f"enum class {name} {{ {values} }}", ""]
            continue
        required = set(d.get("required", []))
        out += ["@Serializable", f"data class {name}("]
        for pname, prop in d["properties"].items():
            out += kt_doc(prop, "    ")
            typ = kt_type(prop, module)
            tail = "" if pname in required else "? = null"
            out.append(f'    @SerialName("{pname}") val {camel(pname)}: {typ}{tail},')
        out += [")", ""]
    return "\n".join(out)


# ---------- Python ----------

def py_type(prop: dict, current: str) -> str:
    if "$ref" in prop:
        return ref_target(prop["$ref"], current)[1]
    t = prop["type"]
    if t == "array":
        return f"list[{py_type(prop['items'], current)}]"
    return {"string": "str", "integer": "int", "number": "float", "boolean": "bool"}[t]


def py_refs(d: dict, module: str) -> set[tuple[str, str]]:
    found = set()
    for prop in d.get("properties", {}).values():
        target = prop.get("items", prop)
        if "$ref" in target:
            found.add(ref_target(target["$ref"], module))
    return found


def py_order(module: str, defs: dict) -> list[str]:
    """Свои ссылки раньше тех, кто на них ссылается."""
    done: list[str] = []

    def visit(name: str) -> None:
        if name in done:
            return
        for mod, dep in py_refs(defs[name], module):
            if mod == module:
                visit(dep)
        done.append(name)

    for name in defs:
        visit(name)
    return done


def py_file(module: str, defs: dict) -> str:
    imports: dict[str, set[str]] = {}
    for d in defs.values():
        for mod, name in py_refs(d, module):
            if mod != module:
                imports.setdefault(mod, set()).add(name)
    out = [f"# {HEADER.format(src=module + '.schema.json')}", "from __future__ import annotations", ""]
    if any("enum" in d for d in defs.values()):
        out += ["from enum import Enum", ""]
    out.append("from pydantic import BaseModel")
    out += [f"from .{mod} import {', '.join(sorted(names))}" for mod, names in sorted(imports.items())]
    out.append("")
    for name in py_order(module, defs):
        d = defs[name]
        out.append("")
        if "enum" in d:
            out.append(f"class {name}(str, Enum):")
            out += [f'    """{d["description"]}"""'] if "description" in d else []
            out += [f'    {v.upper()} = "{v}"' for v in d["enum"]]
            continue
        required = set(d.get("required", []))
        out.append(f"class {name}(BaseModel):")
        out += [f'    """{d["description"]}"""'] if "description" in d else []
        for pname, prop in d["properties"].items():
            typ = py_type(prop, module)
            out.append(f"    {pname}: {typ}" if pname in required else f"    {pname}: {typ} | None = None")
        out.append("")
    return "\n".join(out).rstrip() + "\n"


def py_init(schemas: dict) -> str:
    lines = [f"# {HEADER.format(src='*.schema.json')}"]
    names = []
    for module, defs in schemas.items():
        lines.append(f"from .{module} import {', '.join(defs)}")
        names += list(defs)
    lines.append(f"__all__ = {sorted(names)!r}")
    return "\n".join(lines) + "\n"


def render() -> dict[Path, str]:
    schemas = load_schemas()
    files = {PY_OUT / "__init__.py": py_init(schemas)}
    for module, defs in schemas.items():
        files[KT_OUT / f"{module.capitalize()}.kt"] = kt_file(module, defs)
        files[PY_OUT / f"{module}.py"] = py_file(module, defs)
    return files


def main() -> int:
    files = render()
    existing = set(KT_OUT.glob("*.kt")) | set(PY_OUT.glob("*.py"))
    stale = [p for p, text in files.items() if not p.exists() or p.read_text() != text]
    extra = sorted(existing - set(files))
    if "--check" in sys.argv:
        for p in stale + extra:
            print(f"устарело: {p.relative_to(ROOT)}")
        return 1 if stale or extra else 0
    for p in extra:
        p.unlink()
    for p, text in files.items():
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(text)
    print(f"сгенерировано файлов: {len(files)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
