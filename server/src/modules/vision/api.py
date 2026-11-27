from typing import Protocol

from shared.contracts_gen import PageScript, VisionRequest


class Vision(Protocol):
    """Картинка страницы → сценарий с эмоциями и персонажами по смыслу."""

    def read(self, request: VisionRequest) -> PageScript: ...
