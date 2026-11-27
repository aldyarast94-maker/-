# СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/account.schema.json. Руками не править.
from __future__ import annotations

from pydantic import BaseModel
from .common import Tier


class SignInRequest(BaseModel):
    """Вход через Google."""
    google_id_token: str


class Entitlement(BaseModel):
    """Что доступно пользователю сейчас."""
    tier: Tier
    pages_left: int


class Session(BaseModel):
    """Ответ на вход: токен для запросов и текущий уровень."""
    user_id: str
    token: str
    entitlement: Entitlement


class PurchaseVerifyRequest(BaseModel):
    """Чек покупки из Google Play для проверки на сервере."""
    product_id: str
    purchase_token: str
