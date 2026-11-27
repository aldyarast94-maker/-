from typing import Protocol


class Cache(Protocol):
    """Ключ (хеш страницы/реплики/голоса) → готовый результат или None."""

    def get(self, key: str) -> bytes | None: ...

    def put(self, key: str, value: bytes) -> None: ...
