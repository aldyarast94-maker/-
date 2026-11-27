from typing import Protocol

from shared.contracts_gen import SpeechRequest, SpeechResult


class Speech(Protocol):
    """Произносимый текст + голос + эмоция → звук."""

    def synthesize(self, request: SpeechRequest) -> SpeechResult: ...
