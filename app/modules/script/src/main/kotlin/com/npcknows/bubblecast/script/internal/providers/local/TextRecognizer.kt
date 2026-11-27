package com.npcknows.bubblecast.script.internal.providers.local

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.npcknows.bubblecast.script.internal.image.RgbImage
import java.io.File
import java.nio.FloatBuffer

/** Распознанная строка и уверенность (средняя вероятность символов, 0..1). */
internal data class RecognizedLine(val text: String, val confidence: Float)

/**
 * Одна строка → текст. eslav_PP-OCRv5_mobile_rec: высота 48, ширина по пропорции, BGR, (c/255 − 0.5)/0.5.
 * Выход — вероятности символов по шагам; жадная CTC-расшифровка: 0 — «пусто», затем словарь, в конце пробел.
 */
internal class TextRecognizer(model: File, dictionary: File) : AutoCloseable {
    private val env = OrtEnvironment.getEnvironment()
    private val session = env.createSession(model.path, OrtSession.SessionOptions())
    private val chars: List<String> = listOf("") + dictionary.readLines(Charsets.UTF_8) + listOf(" ")

    fun recognize(line: RgbImage): RecognizedLine {
        val w = (HEIGHT * line.width / line.height).coerceIn(MIN_WIDTH, MAX_WIDTH)
        val input = line.resize(w, HEIGHT).toChw(HALF, HALF, bgr = true)
        OnnxTensor.createTensor(env, FloatBuffer.wrap(input), longArrayOf(1, 3, HEIGHT.toLong(), w.toLong())).use { x ->
            session.run(mapOf("x" to x)).use { r ->
                val out = r[0] as OnnxTensor
                val classes = out.info.shape[2].toInt()
                val probs = out.floatBuffer
                val steps = probs.remaining() / classes
                val text = StringBuilder()
                var prev = -1
                var confidence = 0.0
                for (t in 0 until steps) {
                    var best = 0
                    var bestP = probs[t * classes]
                    for (c in 1 until classes) {
                        val p = probs[t * classes + c]
                        if (p > bestP) { bestP = p; best = c }
                    }
                    confidence += bestP
                    if (best != prev && best != 0 && best < chars.size) text.append(chars[best])
                    prev = best
                }
                return RecognizedLine(text.toString().trim(), (confidence / maxOf(steps, 1)).toFloat())
            }
        }
    }

    override fun close() = session.close()

    private companion object {
        const val HEIGHT = 48
        const val MIN_WIDTH = 16
        const val MAX_WIDTH = 3200
        val HALF = floatArrayOf(0.5f, 0.5f, 0.5f)
    }
}
