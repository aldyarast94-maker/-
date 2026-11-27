# my_voice

**Задача:** запись голоса пользователя + согласие → свой голос (номер профиля)

- Публичный вход: `MyVoice` (`src/main/kotlin/com/npcknows/bubblecast/my_voice/MyVoice.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, api_client.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
