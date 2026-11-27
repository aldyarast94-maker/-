# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/speech.schema.json. Руками не править.
from __future__ import annotations

from pydantic import BaseModel
from .common import Emotion, Lang, Manner


class SpeechRequest(BaseModel):
    """Запрос облачного синтеза одной реплики (Профи)."""
    text: str
    lang: Lang
    voice_id: str
    emotion: Emotion
    manner: Manner
    intensity: float


class SpeechResult(BaseModel):
    """Готовый звук реплики."""
    audio_base64: str
    mime: str
    duration_ms: int
