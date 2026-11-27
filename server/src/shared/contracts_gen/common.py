# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/common.schema.json. Руками не править.
from __future__ import annotations

from enum import Enum

from pydantic import BaseModel


class Lang(str, Enum):
    """Язык реплики. Пока два."""
    RU = "ru"
    EN = "en"

class LineType(str, Enum):
    """Тип реплики: речь, мысль, текст автора, звук, надпись на фоне."""
    DIALOGUE = "dialogue"
    THOUGHT = "thought"
    NARRATION = "narration"
    SFX = "sfx"
    SIGN = "sign"

class Emotion(str, Enum):
    """Эмоция, с которой читать реплику."""
    NEUTRAL = "neutral"
    HAPPY = "happy"
    SAD = "sad"
    ANGRY = "angry"
    SCARED = "scared"
    SURPRISED = "surprised"
    DISGUSTED = "disgusted"
    TENDER = "tender"
    SARCASTIC = "sarcastic"

class Manner(str, Enum):
    """Манера подачи: обычно, шёпотом, криком."""
    NORMAL = "normal"
    WHISPER = "whisper"
    SHOUT = "shout"

class Tier(str, Enum):
    """Уровень: Базовый (на телефоне) или Профи (подписка)."""
    BASIC = "basic"
    PRO = "pro"

class BBox(BaseModel):
    """Рамка на странице в долях от ширины и высоты (0..1)."""
    x: float
    y: float
    w: float
    h: float
