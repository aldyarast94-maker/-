from fastapi.testclient import TestClient

from modules.api.api import create_app


def test_health() -> None:
    assert TestClient(create_app()).get("/health").json() == {"status": "ok"}
