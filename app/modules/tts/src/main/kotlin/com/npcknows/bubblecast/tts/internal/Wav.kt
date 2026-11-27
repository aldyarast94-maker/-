package com.npcknows.bubblecast.tts.internal

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.roundToInt

/** Звук в WAV (16 бит, моно). */
internal object Wav {
    const val MIME = "audio/wav"

    /** Нормирует громкость так, чтобы пик был [peak] (0..1). Тишину не трогает. */
    fun normalize(samples: FloatArray, peak: Float): FloatArray {
        val max = samples.maxOfOrNull(::abs) ?: return samples
        if (max < 1e-4f) return samples
        val k = peak / max
        return FloatArray(samples.size) { samples[it] * k }
    }

    fun encode(samples: FloatArray, sampleRate: Int): ByteArray {
        val data = samples.size * 2
        val buf = ByteBuffer.allocate(44 + data).order(ByteOrder.LITTLE_ENDIAN)
        buf.put("RIFF".toByteArray()).putInt(36 + data).put("WAVE".toByteArray())
        buf.put("fmt ".toByteArray()).putInt(16).putShort(1).putShort(1)
            .putInt(sampleRate).putInt(sampleRate * 2).putShort(2).putShort(16)
        buf.put("data".toByteArray()).putInt(data)
        for (s in samples) buf.putShort((s.coerceIn(-1f, 1f) * Short.MAX_VALUE).roundToInt().toShort())
        return buf.array()
    }
}
