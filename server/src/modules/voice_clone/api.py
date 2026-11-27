from typing import Protocol

from shared.contracts_gen import VoiceCloneRequest, VoiceProfile


class VoiceClone(Protocol):
    """Запись голоса + согласие → профиль голоса. Только свой голос пользователя."""

    def create(self, user_id: str, request: VoiceCloneRequest) -> VoiceProfile: ...

    def delete(self, user_id: str, voice_id: str) -> None: ...
