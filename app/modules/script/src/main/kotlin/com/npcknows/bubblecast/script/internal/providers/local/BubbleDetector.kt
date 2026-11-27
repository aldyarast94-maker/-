package com.npcknows.bubblecast.script.internal.providers.local

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.npcknows.bubblecast.script.internal.image.RgbImage
import java.io.File
import java.nio.FloatBuffer
import java.nio.LongBuffer

/** Текст внутри пузыря или вне пузыря (подписи, звуки, текст автора). Рамка — в пикселях страницы. */
internal data class TextRegion(val x0: Float, val y0: Float, val x1: Float, val y1: Float, val inBubble: Boolean, val score: Float)

/**
 * Детектор ogkalu/comic-text-and-bubble-detector (RT-DETR-v2): вход 640×640 RGB/255, рамки сразу в пикселях.
 * Классы: 0 — пузырь, 1 — текст в пузыре, 2 — текст вне пузыря. Нам нужен только текст (1 и 2).
 * Длинную ленту вебтуна режем на почти квадратные куски — иначе при сжатии до 640×640 текст пропадает.
 */
internal class BubbleDetector(model: File) : AutoCloseable {
    private val env = OrtEnvironment.getEnvironment()
    private val session = env.createSession(model.path, OrtSession.SessionOptions())

    fun detect(image: RgbImage): List<TextRegion> {
        if (image.height <= image.width * TALL_RATIO) return detectTile(image, 0)
        val step = (image.width * (1 - TILE_OVERLAP)).toInt()
        val found = mutableListOf<TextRegion>()
        var top = 0
        while (true) {
            val bottom = minOf(top + image.width, image.height)
            found += detectTile(image.crop(0, top, image.width, bottom), top)
            if (bottom == image.height) break
            top += step
        }
        return dedupe(found)
    }

    private fun detectTile(tile: RgbImage, offsetY: Int): List<TextRegion> {
        val input = tile.resize(SIZE, SIZE).toChw(ZERO, ONE, bgr = false)
        OnnxTensor.createTensor(env, FloatBuffer.wrap(input), longArrayOf(1, 3, SIZE.toLong(), SIZE.toLong())).use { images ->
            OnnxTensor.createTensor(env, LongBuffer.wrap(longArrayOf(tile.width.toLong(), tile.height.toLong())), longArrayOf(1, 2)).use { sizes ->
                session.run(mapOf("images" to images, "orig_target_sizes" to sizes)).use { r ->
                    val labels = (r.get("labels").get() as OnnxTensor).longBuffer
                    val boxes = (r.get("boxes").get() as OnnxTensor).floatBuffer
                    val scores = (r.get("scores").get() as OnnxTensor).floatBuffer
                    return (0 until scores.remaining()).mapNotNull { i ->
                        val label = labels[i].toInt()
                        val score = scores[i]
                        if (score < MIN_SCORE || label == BUBBLE) return@mapNotNull null
                        TextRegion(
                            boxes[i * 4], boxes[i * 4 + 1] + offsetY, boxes[i * 4 + 2], boxes[i * 4 + 3] + offsetY,
                            inBubble = label == TEXT_IN_BUBBLE, score = score,
                        )
                    }
                }
            }
        }
    }

    /** Куски ленты перекрываются — одну рамку могли найти дважды. Оставляем более уверенную. */
    private fun dedupe(regions: List<TextRegion>): List<TextRegion> {
        val kept = mutableListOf<TextRegion>()
        for (r in regions.sortedByDescending { it.score }) if (kept.none { iou(it, r) > 0.5f }) kept += r
        return kept
    }

    private fun iou(a: TextRegion, b: TextRegion): Float {
        val w = minOf(a.x1, b.x1) - maxOf(a.x0, b.x0)
        val h = minOf(a.y1, b.y1) - maxOf(a.y0, b.y0)
        if (w <= 0 || h <= 0) return 0f
        val inter = w * h
        return inter / ((a.x1 - a.x0) * (a.y1 - a.y0) + (b.x1 - b.x0) * (b.y1 - b.y0) - inter)
    }

    override fun close() = session.close()

    private companion object {
        const val SIZE = 640
        const val MIN_SCORE = 0.5f
        const val BUBBLE = 0
        const val TEXT_IN_BUBBLE = 1
        const val TALL_RATIO = 1.6f
        const val TILE_OVERLAP = 0.2f
        val ZERO = floatArrayOf(0f, 0f, 0f)
        val ONE = floatArrayOf(1f, 1f, 1f)
    }
}
