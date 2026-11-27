package com.npcknows.bubblecast.tts

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.AudioClip
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.Voice

/** Произносимый текст + голос + эмоция → звук. Поставщики: local (sherpa-onnx) и cloud (сервер, Профи). */
interface Synthesizer {
    suspend fun synthesize(
        text: SpeakableText,
        voice: Voice,
        emotion: Emotion,
        manner: Manner,
        intensity: Double,
        tier: Tier,
    ): AudioClip
}
