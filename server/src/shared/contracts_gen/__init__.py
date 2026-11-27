# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/*.schema.json. Руками не править.
from .account import SignInRequest, Session, PurchaseVerifyRequest, Entitlement
from .common import Lang, LineType, Emotion, Manner, Tier, BBox
from .script import Line, PageScript, VisionRequest
from .speech import SpeechRequest, SpeechResult
from .voice import VoiceCloneRequest, VoiceProfile
__all__ = ['BBox', 'Emotion', 'Entitlement', 'Lang', 'Line', 'LineType', 'Manner', 'PageScript', 'PurchaseVerifyRequest', 'Session', 'SignInRequest', 'SpeechRequest', 'SpeechResult', 'Tier', 'VisionRequest', 'VoiceCloneRequest', 'VoiceProfile']
