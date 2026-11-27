package com.npcknows.bubblecast.tts.internal

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Manner

/**
 * Как подать реплику, когда модель не умеет эмоции напрямую: темп и громкость.
 * Сила эмоции (0..1) плавно смешивает обычную подачу с эмоциональной.
 */
internal data class Delivery(val speed: Float, val peak: Float) {
    companion object {
        private const val BASE_SPEED = 1.05f

        fun of(emotion: Emotion, manner: Manner, intensity: Double): Delivery {
            val k = intensity.coerceIn(0.0, 1.0).toFloat()
            val tempo = when (emotion) {
                Emotion.NEUTRAL -> 1.0f
                Emotion.HAPPY -> 1.08f
                Emotion.SAD -> 0.9f
                Emotion.ANGRY -> 1.12f
                Emotion.SCARED -> 1.15f
                Emotion.SURPRISED -> 1.1f
                Emotion.DISGUSTED -> 0.97f
                Emotion.TENDER -> 0.93f
                Emotion.SARCASTIC -> 0.96f
            }
            val (mannerTempo, peak) = when (manner) {
                Manner.NORMAL -> 1.0f to 0.8f
                Manner.WHISPER -> 0.95f to 0.4f
                Manner.SHOUT -> 1.05f to 0.97f
            }
            return Delivery(speed = BASE_SPEED * (1 + (tempo - 1) * k) * mannerTempo, peak = peak)
        }
    }
}
