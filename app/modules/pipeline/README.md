# pipeline

**Задача:** страница → готовые к проигрыванию реплики. **Только вызывает шаги по порядку**

- Публичный вход: `PagePipeline` (`src/main/kotlin/com/npcknows/bubblecast/pipeline/PagePipeline.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, script, characters, cast, text_prep, tts.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
