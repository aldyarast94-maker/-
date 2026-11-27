# account

**Задача:** вход через Google → пользователь и его токен

- Публичный вход: `Account` (`src/main/kotlin/com/npcknows/bubblecast/account/Account.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, api_client.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
