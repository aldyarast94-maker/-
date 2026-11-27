# overlay

**Задача:** действия пользователя → кнопки поверх других приложений (старт, пауза, стоп)

- Публичный вход: `Overlay` (`src/main/kotlin/com/npcknows/bubblecast/overlay/Overlay.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared.
- Вид: `bubblecast.android.ui`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
