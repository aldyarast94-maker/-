# drive_mode

**Задача:** действия пользователя → экран для дороги: крупные кнопки, можно без экрана

- Публичный вход: `DriveMode` (`src/main/kotlin/com/npcknows/bubblecast/drive_mode/DriveMode.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, reading_session.
- Вид: `bubblecast.android.ui`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
