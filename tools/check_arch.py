#!/usr/bin/env python3
"""Сверяет arch.md с кодом. Ноль ошибок — карта и код совпадают.

    python3 tools/check_arch.py

Проверяет:
- папки модулей = строки таблиц в arch.md (приложение и сервер), у каждого есть README;
- зависимости в build.gradle.kts = колонка «Зависит от»;
- импорты Kotlin и Python: только разрешённые модули, только их публичный вход, без чужого internal;
- shared не зависит от модулей; в карте нет циклов.
"""
from __future__ import annotations

import ast
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
APP = ROOT / "app/modules"
SERVER = ROOT / "server/src/modules"
KT_PREFIX = "com.npcknows.bubblecast."
ROW = re.compile(r"^\| `(\w+)` \| .+? \| (.+?) \| .+? \|$", re.M)

errors: list[str] = []


def err(msg: str) -> None:
    errors.append(msg)


def table(part: str) -> dict[str, set[str]]:
    return {name: {d.strip() for d in deps.split(",")} - {"shared"} for name, deps in ROW.findall(part)}


def load_map() -> tuple[dict[str, set[str]], dict[str, set[str]]]:
    text = (ROOT / "arch.md").read_text()
    app_part = text.split("### Приложение")[1].split("### Сервер")[0]
    server_part = text.split("### Сервер")[1].split("\n## ")[0]
    return table(app_part), table(server_part)


def check_cycles(side: str, graph: dict[str, set[str]]) -> None:
    state: dict[str, int] = {}

    def visit(node: str, path: list[str]) -> None:
        if state.get(node) == 1:
            err(f"{side}: цикл зависимостей {' → '.join(path + [node])}")
            return
        if state.get(node) == 2:
            return
        state[node] = 1
        for dep in sorted(graph.get(node, ())):
            if dep not in graph:
                err(f"{side}: модуль {node} зависит от {dep}, которого нет в карте")
            else:
                visit(dep, path + [node])
        state[node] = 2

    for node in sorted(graph):
        visit(node, [])


def check_folders(side: str, base: Path, graph: dict[str, set[str]], must_have: list[str]) -> None:
    folders = {p.name for p in base.iterdir() if p.is_dir() and not p.name.startswith("__")}
    for name in sorted(folders - graph.keys()):
        err(f"{side}: папка {name} есть, а в карте модуля нет")
    for name in sorted(graph.keys() - folders):
        err(f"{side}: модуль {name} есть в карте, а папки нет")
    for name in sorted(folders & graph.keys()):
        for f in must_have:
            if not (base / name / f).exists():
                err(f"{side}/{name}: нет {f}")


# ---------- приложение ----------

def kt_imports(path: Path) -> list[str]:
    return re.findall(r"^import\s+([\w.]+)", path.read_text(), re.M)


def check_app_imports(owner: str, files: list[Path], allowed: set[str], is_shared: bool) -> None:
    for f in files:
        for imp in kt_imports(f):
            if not imp.startswith(KT_PREFIX):
                continue
            target = imp[len(KT_PREFIX):].split(".")[0]
            if target in {owner, "shared", "contracts"}:
                continue
            where = f.relative_to(ROOT)
            if is_shared:
                err(f"{where}: shared не должен импортировать модуль {target}")
            elif target not in allowed:
                err(f"{where}: импорт {imp} — {owner} не зависит от {target} по карте")
            elif ".internal" in imp:
                err(f"{where}: импорт чужого internal {imp}")


def check_app(graph: dict[str, set[str]]) -> None:
    check_folders("app", APP, graph, ["README.md", "build.gradle.kts"])
    for name, allowed in graph.items():
        build = APP / name / "build.gradle.kts"
        if not build.exists():
            continue
        gradle_deps = set(re.findall(r'project\(":modules:(\w+)"\)', build.read_text()))
        if gradle_deps != allowed:
            extra = ", ".join(sorted(gradle_deps - allowed)) or "—"
            missing = ", ".join(sorted(allowed - gradle_deps)) or "—"
            err(f"app/{name}/build.gradle.kts не совпадает с картой: лишние {extra}; не хватает {missing}")
        check_app_imports(name, sorted((APP / name).rglob("*.kt")), allowed, False)
    shared_src = ROOT / "app/shared/src"
    check_app_imports("shared", sorted(shared_src.rglob("*.kt")), set(), True)
    if "project(" in (ROOT / "app/shared/build.gradle.kts").read_text():
        err("app/shared/build.gradle.kts: shared не должен зависеть от модулей")


# ---------- сервер ----------

def py_imports(path: Path) -> list[str]:
    tree = ast.parse(path.read_text(), filename=str(path))
    found: list[str] = []
    for node in ast.walk(tree):
        if isinstance(node, ast.Import):
            found += [a.name for a in node.names]
        elif isinstance(node, ast.ImportFrom) and node.module and node.level == 0:
            found.append(node.module)
    return found


def check_server(graph: dict[str, set[str]]) -> None:
    check_folders("server", SERVER, graph, ["README.md", "api.py"])
    for name, allowed in graph.items():
        for f in sorted((SERVER / name).rglob("*.py")) if (SERVER / name).exists() else []:
            for imp in py_imports(f):
                parts = imp.split(".")
                if parts[0] != "modules" or len(parts) < 2 or parts[1] == name:
                    continue
                target, where = parts[1], f.relative_to(ROOT)
                if target not in allowed:
                    err(f"{where}: импорт {imp} — {name} не зависит от {target} по карте")
                elif imp != f"modules.{target}.api":
                    err(f"{where}: импорт {imp} — у чужого модуля можно брать только modules.{target}.api")
    for f in sorted((ROOT / "server/src/shared").rglob("*.py")):
        if any(i.split(".")[0] == "modules" for i in py_imports(f)):
            err(f"{f.relative_to(ROOT)}: shared не должен импортировать модули")


def main() -> int:
    app_graph, server_graph = load_map()
    if not app_graph or not server_graph:
        print("не нашёл таблицы модулей в arch.md")
        return 1
    check_cycles("app", app_graph)
    check_cycles("server", server_graph)
    check_app(app_graph)
    check_server(server_graph)
    for e in errors:
        print(f"ОШИБКА: {e}")
    print(f"модулей: приложение {len(app_graph)}, сервер {len(server_graph)}; ошибок: {len(errors)}")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
