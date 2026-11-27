# billing

**Задача:** покупка в Google Play → уровень (Базовый/Профи) и остаток лимита

- Публичный вход: `Billing` (`src/main/kotlin/com/npcknows/bubblecast/billing/Billing.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, api_client.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
