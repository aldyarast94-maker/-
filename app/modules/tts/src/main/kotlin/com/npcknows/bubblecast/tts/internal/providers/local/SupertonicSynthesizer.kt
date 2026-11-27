package com.npcknows.bubblecast.tts.internal.providers.local

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.AudioClip
import com.npcknows.bubblecast.shared.Gender
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.ModelLocator
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId
import com.npcknows.bubblecast.shared.VoiceOrigin
import com.npcknows.bubblecast.tts.Synthesizer
import com.npcknows.bubblecast.tts.internal.Delivery
import com.npcknows.bubblecast.tts.internal.Wav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Supertonic-3 на телефоне, без сети. Модели загружаются при первой реплике. */
internal class SupertonicSynthesizer(private val models: ModelLocator) : Synthesizer {
    private val lock = Mutex()
    private var engine: SupertonicEngine? = null
    private val styles = mutableMapOf<String, VoiceStyle>()

    override fun voices(): List<Voice> = ModelCatalog.TTS_VOICES.keys.map { key ->
        val female = key.startsWith("F")
        Voice(
            id = VoiceId("$PREFIX$key"),
            name = (if (female) "Женский " else "Мужской ") + key.drop(1),
            langs = setOf(Lang.RU, Lang.EN),
            gender = if (female) Gender.FEMALE else Gender.MALE,
            origin = VoiceOrigin.STANDARD,
        )
    }

    override suspend fun synthesize(
        text: SpeakableText, voice: Voice, emotion: Emotion, manner: Manner, intensity: Double, tier: Tier,
    ): AudioClip = withContext(Dispatchers.Default) {
        lock.withLock {
            val e = engine ?: open().also { engine = it }
            val key = voice.id.value.removePrefix(PREFIX)
            val styleFile = requireNotNull(ModelCatalog.TTS_VOICES[key]) { "неизвестный голос ${voice.id.value}" }
            val style = styles.getOrPut(key) { e.loadStyle(models.file(styleFile)) }
            val delivery = Delivery.of(emotion, manner, intensity)
            val lang = when (text.lang) { Lang.RU -> "ru"; Lang.EN -> "en" }
            val raw = e.synthesize(text.text, lang, style, STEPS, delivery.speed, seed = text.text.hashCode().toLong())
            val samples = Wav.normalize(raw, delivery.peak)
            AudioClip(Wav.encode(samples, e.sampleRate), Wav.MIME, samples.size * 1000 / e.sampleRate)
        }
    }

    private fun open() = SupertonicEngine(
        models.file(ModelCatalog.TTS_DURATION_PREDICTOR),
        models.file(ModelCatalog.TTS_TEXT_ENCODER),
        models.file(ModelCatalog.TTS_VECTOR_ESTIMATOR),
        models.file(ModelCatalog.TTS_VOCODER),
        models.file(ModelCatalog.TTS_CONFIG),
        models.file(ModelCatalog.TTS_INDEXER),
    )

    private companion object {
        const val PREFIX = "supertonic-"
        /** Шагов очистки шума: 8 — как в примере Supertone; меньше — быстрее, но хуже. Подберём на телефоне. */
        const val STEPS = 8
    }
}
