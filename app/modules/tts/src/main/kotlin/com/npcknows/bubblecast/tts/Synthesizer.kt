package com.npcknows.bubblecast.tts

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.AudioClip
import com.npcknows.bubblecast.shared.ModelLocator
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId

/** Произносимый текст + голос + эмоция → звук. Поставщики: local (Supertonic-3 на телефоне) и cloud (сервер, Профи). */
interface Synthesizer {
    /** Голоса, которые можно использовать сейчас (их получает cast). */
    fun voices(): List<Voice>

    suspend fun synthesize(
        text: SpeakableText,
        voice: Voice,
        emotion: Emotion,
        manner: Manner,
        intensity: Double,
        tier: Tier,
    ): AudioClip
}

/** Стандартные голоса чтеца для первого запуска. */
object StandardVoices {
    val MALE = VoiceId("supertonic-M1")
    val FEMALE = VoiceId("supertonic-F1")
}

/** Готовый синтезатор для сборки app/. Облачный поставщик подключится на этапе 7. */
fun Synthesizer(models: ModelLocator): Synthesizer =
    com.npcknows.bubblecast.tts.internal.DefaultSynthesizer(
        com.npcknows.bubblecast.tts.internal.providers.local.SupertonicSynthesizer(models),
    )
