# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/script.schema.json. Руками не править.
from __future__ import annotations

from pydantic import BaseModel
from .common import BBox, Emotion, Lang, LineType, Manner


class Line(BaseModel):
    """Одна реплика на странице в порядке чтения."""
    id: str
    bbox: BBox
    text: str
    lang: Lang
    type: LineType
    speaker_hint: str | None = None
    emotion: Emotion
    manner: Manner
    intensity: float


class PageScript(BaseModel):
    """Все реплики страницы в порядке чтения."""
    page_hash: str
    lines: list[Line]


class VisionRequest(BaseModel):
    """Запрос облачного понимания страницы (Профи)."""
    page_hash: str
    image_base64: str
    image_mime: str
