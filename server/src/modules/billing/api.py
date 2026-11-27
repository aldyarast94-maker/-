from typing import Protocol

from shared.contracts_gen import Entitlement, PurchaseVerifyRequest


class Billing(Protocol):
    """Чек покупки Google Play → подписка и лимит."""

    def entitlement(self, user_id: str) -> Entitlement: ...

    def verify(self, user_id: str, request: PurchaseVerifyRequest) -> Entitlement: ...

    def spend_page(self, user_id: str) -> bool:
        """Списать одну облачную страницу. False — лимит кончился."""
        ...
