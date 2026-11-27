# narration

**Задача:** готовые реплики + видимая область → что и когда играть, какую реплику подсветить

- Публичный вход: `Narrator` (`src/main/kotlin/com/npcknows/bubblecast/narration/Narrator.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, audio_session.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
