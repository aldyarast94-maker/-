# tts

**Задача:** произносимый текст + голос + эмоция → звук

- Публичный вход: `Synthesizer` (`src/main/kotlin/com/npcknows/bubblecast/tts/Synthesizer.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, api_client.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Поставщики

- `internal/providers/local/` — на телефоне, без сети (Базовый).
- `internal/providers/cloud/` — через `api_client` (Профи).

## Лицензии моделей и голосов

Нет (модуль пустой).
