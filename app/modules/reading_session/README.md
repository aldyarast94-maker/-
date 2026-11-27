# reading_session

**Задача:** выбранный источник + позиция чтения → команды pipeline, reader и narration. **Только связывает**

- Публичный вход: `ReadingSession` (`src/main/kotlin/com/npcknows/bubblecast/reading_session/ReadingSession.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, reader, pipeline, narration, overlay, library.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
