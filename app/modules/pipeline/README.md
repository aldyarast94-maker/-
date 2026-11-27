# pipeline

**Задача:** страница → готовые к проигрыванию реплики. **Только вызывает шаги по порядку**

- Публичный вход: `PagePipeline` (`src/main/kotlin/com/npcknows/bubblecast/pipeline/PagePipeline.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, script, characters, cast, text_prep, tts.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Как работает сейчас

script → для каждой реплики: text_prep → characters → cast → tts. Надписи на фоне (`sign`) и реплики,
от которых после подготовки ничего не осталось, пропускаются. Реплики отдаются по одной, как только готов звук.

## Лицензии моделей и голосов

Нет (модуль пустой).
