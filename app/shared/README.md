# shared (приложение)

Общее для модулей приложения — **единственное место**. Сюда попадает только то, что нужно
двум и более модулям. Не зависит ни от одного модуля.

- `contracts_gen/` — типы из `contracts/`, сгенерированы, руками не править.
- `src/main/kotlin/.../shared/` — общие типы телефона (не ходят на сервер):

| Тип | Кому нужен |
|---|---|
| `Page`, `PageImage`, `Layout`, `PageSource` | источники, reader, pipeline, script, reading_session |
| `VisibleArea` | reader, narration, reading_session |
| `Character`, `CharacterId`, `Gender`, `AgeGroup` | characters, cast, voices |
| `Voice`, `VoiceId`, `VoiceOrigin` | cast, tts, voices, my_voice, onboarding, settings |
| `SpeakableText` | text_prep, tts, pipeline |
| `AudioClip` | tts, pipeline, narration, audio_session |
| `PlayableLine` | pipeline, narration, reading_session |
| `NarrationMode` | narration, reading_session, drive_mode, settings |

Вид: чистый Kotlin.
