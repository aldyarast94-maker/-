package com.npcknows.bubblecast.script.internal.providers.local

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.script.internal.ReadingOrder
import com.npcknows.bubblecast.script.internal.image.RgbImage
import java.io.File
import java.nio.FloatBuffer
import kotlin.math.roundToInt

/**
 * Строки текста внутри найденной рамки: PP-OCRv5_mobile_det (DB) даёт карту «здесь текст»,
 * пятна на карте → прямоугольники, пятна одного ряда (слова одной строки) склеиваются в строку.
 * Рамки строк — в пикселях переданной картинки, сверху вниз.
 */
internal class LineFinder(model: File) : AutoCloseable {
    private val env = OrtEnvironment.getEnvironment()
    private val session = env.createSession(model.path, OrtSession.SessionOptions())

    fun lines(crop: RgbImage): List<BBox> {
        val scale = if (maxOf(crop.width, crop.height) > MAX_SIDE) MAX_SIDE.toFloat() / maxOf(crop.width, crop.height) else 1f
        val w = roundTo32(crop.width * scale)
        val h = roundTo32(crop.height * scale)
        val input = crop.resize(w, h).toChw(MEAN, STD, bgr = true)
        val prob = OnnxTensor.createTensor(env, FloatBuffer.wrap(input), longArrayOf(1, 3, h.toLong(), w.toLong())).use { x ->
            session.run(mapOf("x" to x)).use { r -> (r[0] as OnnxTensor).floatBuffer.let { b -> FloatArray(b.remaining()).also(b::get) } }
        }
        val sx = crop.width.toDouble() / w
        val sy = crop.height.toDouble() / h
        val blobs = blobs(prob, w, h).map { b ->
            BBox(b.x * sx, b.y * sy, b.w * sx, b.h * sy)
        }
        return ReadingOrder.rows(blobs) { it }.map(::union)
    }

    /** Связные пятна карты вероятностей (порог 0.3), уверенные (средняя ≥ 0.6), с запасом по краю как в DB. */
    private fun blobs(prob: FloatArray, w: Int, h: Int): List<BBox> {
        val seen = BooleanArray(prob.size)
        val queue = IntArray(prob.size)
        val out = mutableListOf<BBox>()
        for (start in prob.indices) {
            if (seen[start] || prob[start] <= THRESH) continue
            var head = 0
            var tail = 0
            queue[tail++] = start
            seen[start] = true
            var minX = w; var minY = h; var maxX = 0; var maxY = 0
            var sum = 0.0
            while (head < tail) {
                val p = queue[head++]
                val x = p % w
                val y = p / w
                sum += prob[p]
                if (x < minX) minX = x; if (x > maxX) maxX = x
                if (y < minY) minY = y; if (y > maxY) maxY = y
                for (n in intArrayOf(p - 1, p + 1, p - w, p + w)) {
                    if (n < 0 || n >= prob.size || seen[n] || prob[n] <= THRESH) continue
                    if ((n == p - 1 && x == 0) || (n == p + 1 && x == w - 1)) continue
                    seen[n] = true
                    queue[tail++] = n
                }
            }
            if (tail < MIN_PIXELS || sum / tail < BOX_THRESH) continue
            val bw = (maxX - minX + 1).toDouble()
            val bh = (maxY - minY + 1).toDouble()
            val pad = bw * bh * UNCLIP / (2 * (bw + bh))
            out += BBox(minX - pad, minY - pad, bw + 2 * pad, bh + 2 * pad)
        }
        return out
    }

    private fun union(row: List<BBox>): BBox {
        val x0 = row.minOf { it.x }
        val y0 = row.minOf { it.y }
        return BBox(x0, y0, row.maxOf { it.x + it.w } - x0, row.maxOf { it.y + it.h } - y0)
    }

    private fun roundTo32(v: Float) = maxOf(32, (v / 32).roundToInt() * 32)

    override fun close() = session.close()

    private companion object {
        const val MAX_SIDE = 960
        const val THRESH = 0.3f
        const val BOX_THRESH = 0.6
        const val MIN_PIXELS = 10
        const val UNCLIP = 1.5
        val MEAN = floatArrayOf(0.485f, 0.456f, 0.406f)
        val STD = floatArrayOf(0.229f, 0.224f, 0.225f)
    }
}
