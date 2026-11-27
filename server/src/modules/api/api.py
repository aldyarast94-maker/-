from dataclasses import dataclass

from fastapi import FastAPI

from modules.accounts.api import Accounts
from modules.billing.api import Billing
from modules.speech.api import Speech
from modules.vision.api import Vision
from modules.voice_clone.api import VoiceClone


@dataclass(frozen=True)
class Deps:
    """Модули, которые api вызывает. Собирает их main.py."""

    accounts: Accounts
    billing: Billing
    vision: Vision
    speech: Speech
    voice_clone: VoiceClone


def create_app(deps: Deps | None = None) -> FastAPI:
    """HTTP-запросы → вызовы модулей. Маршруты Профи появятся на этапе 7."""
    app = FastAPI(title="BubbleCast")

    @app.get("/health")
    def health() -> dict[str, str]:
        return {"status": "ok"}

    return app
