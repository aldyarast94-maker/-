# server

Облачные поставщики уровня Профи. Своего порядка обработки нет — он в приложении (`pipeline`).

    python3 -m venv .venv && . .venv/bin/activate
    pip install -e ".[dev]"
    pytest
    uvicorn main:app --app-dir src
