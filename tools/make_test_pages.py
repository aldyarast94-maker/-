#!/usr/bin/env python3
"""Рисует тестовые страницы комикса с известным текстом (своя графика — без чужих авторских прав).

    python3 tools/make_test_pages.py   # нужен Pillow и шрифт DejaVu Sans Bold

Результат в app/modules/script/src/test/resources/pages/:
- <имя>.png — посмотреть глазами;
- <имя>.rgb.gz — те же пиксели для тестов (ширина и высота — 2 × int32 big-endian, затем RGB);
  тесты Android-модуля собираются без javax.imageio, а декодирование PNG — дело Android, не наше;
- <имя>.json — ожидаемые реплики по порядку.
"""
import gzip
import json
import struct
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

OUT = Path(__file__).resolve().parent.parent / "app/modules/script/src/test/resources/pages"
FONT = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 34)


def bubble(d, box, lines):
    x0, y0, x1, y1 = box
    d.ellipse(box, fill="white", outline="black", width=5)
    y = (y0 + y1) / 2 - len(lines) * 22
    for line in lines:
        d.text(((x0 + x1) / 2 - d.textlength(line, font=FONT) / 2, y), line, fill="black", font=FONT)
        y += 44


def face(d, cx, cy):
    d.ellipse((cx - 70, cy - 70, cx + 70, cy + 70), fill=(230, 200, 170), outline="black", width=4)


def save(name, img, layout, bubbles):
    OUT.mkdir(parents=True, exist_ok=True)
    img.save(OUT / f"{name}.png", optimize=True)
    raw = struct.pack(">ii", *img.size) + img.convert("RGB").tobytes()
    (OUT / f"{name}.rgb.gz").write_bytes(gzip.compress(raw, mtime=0))
    (OUT / f"{name}.json").write_text(json.dumps(
        {"layout": layout, "expected": [" ".join(lines) for _, lines in bubbles]}, ensure_ascii=False, indent=1))


def comic_page():
    """Страница комикса 2×2, RU сверху, EN снизу. Порядок слева направо."""
    img = Image.new("RGB", (1000, 1400), "white")
    d = ImageDraw.Draw(img)
    for box in [(20, 20, 980, 680), (20, 700, 490, 1380), (510, 700, 980, 1380)]:
        d.rectangle(box, outline="black", width=6)
    for c in [(300, 520), (700, 560), (250, 1200), (760, 1220)]:
        face(d, *c)
    bubbles = [
        ((60, 60, 470, 300), ["ПРИВЕТ! ТЫ", "ОПЯТЬ ОПОЗДАЛ?"]),
        ((540, 100, 950, 330), ["ПРОСТИ, АВТОБУС", "СЛОМАЛСЯ..."]),
        ((60, 740, 460, 980), ["WHERE IS", "THE DRAGON?"]),
        ((540, 760, 950, 1000), ["IT FLEW AWAY", "LAST NIGHT!"]),
    ]
    for box, lines in bubbles:
        bubble(d, box, lines)
    save("comic_ru_en", img, "COMIC_LTR", bubbles)


def webtoon_strip():
    """Лента вебтуна 800×2800: три пузыря друг под другом."""
    img = Image.new("RGB", (800, 2800), (245, 245, 255))
    d = ImageDraw.Draw(img)
    for c in [(400, 600), (400, 1500), (400, 2450)]:
        face(d, *c)
    bubbles = [
        ((150, 150, 650, 400), ["ГДЕ Я?", "ЧТО СЛУЧИЛОСЬ?"]),
        ((150, 1050, 650, 1300), ["ТЫ ПРОСПАЛ", "ТРИ ДНЯ."]),
        ((150, 2000, 650, 2250), ["НЕ МОЖЕТ", "БЫТЬ!"]),
    ]
    for box, lines in bubbles:
        bubble(d, box, lines)
    save("webtoon_ru", img, "WEBTOON", bubbles)


if __name__ == "__main__":
    comic_page()
    webtoon_strip()
    print("готово:", sorted(p.name for p in OUT.iterdir()))
