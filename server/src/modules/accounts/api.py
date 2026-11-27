from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class SignedIn:
    user_id: str
    token: str


class Accounts(Protocol):
    """Токен Google → пользователь."""

    def sign_in(self, google_id_token: str) -> SignedIn: ...

    def user_for_token(self, token: str) -> str | None: ...
