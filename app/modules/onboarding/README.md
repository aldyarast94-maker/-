# onboarding

**Задача:** первый запуск → выбранный голос чтеца (мужской / женский / свой) + объяснение разрешений

- Публичный вход: `Onboarding` (`src/main/kotlin/com/npcknows/bubblecast/onboarding/Onboarding.kt`).
  Другие модули используют только его.
- Всё остальное — в пакете `internal/` с видимостью `internal`.
- Зависит от: shared, my_voice, settings.
- Вид: `bubblecast.android.ui`.
- Тесты: `src/test/`.

## Лицензии моделей и голосов

Нет (модуль пустой).
