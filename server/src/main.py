"""Сборка сервера: только соединяет модули. Логики нет.

    uvicorn main:app --app-dir src
"""
from modules.api.api import create_app

# Реализации модулей подключатся на этапе 7 (Профи).
app = create_app()
