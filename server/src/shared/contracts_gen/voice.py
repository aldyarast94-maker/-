# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/voice.schema.json. Руками не править.
from __future__ import annotations

from pydantic import BaseModel
from .common import Lang


class VoiceCloneRequest(BaseModel):
    """Запись голоса пользователя вместе с прочитанной фразой согласия."""
    sample_base64: str
    sample_mime: str
    consent_text: str
    lang: Lang


class VoiceProfile(BaseModel):
    """Созданный голос пользователя."""
    voice_id: str
    created_at: str
    trial_left_chapters: int
