# audio_session

**Задача:** очередь звуков → игра в фоне, экран блокировки, кнопки наушников и руля

- Публичный вход: `AudioSession` (`src/main/kotlin/com/npcknows/bubblecast/audio_session/AudioSession.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared.
- Вид: `bubblecast.android`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
