package com.npcknows.bubblecast.tts.internal

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.AudioClip
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.tts.Synthesizer

/** Выбирает поставщика по уровню. Пока есть только local — им читаются и Базовый, и Профи. */
internal class DefaultSynthesizer(private val local: Synthesizer) : Synthesizer {
    override fun voices(): List<Voice> = local.voices()

    override suspend fun synthesize(
        text: SpeakableText, voice: Voice, emotion: Emotion, manner: Manner, intensity: Double, tier: Tier,
    ): AudioClip = local.synthesize(text, voice, emotion, manner, intensity, tier)
}
