# api_client

**Задача:** запросы модулей → обмен с сервером по контрактам

- Публичный вход: `ApiClient` (`src/main/kotlin/com/npcknows/bubblecast/api_client/ApiClient.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
