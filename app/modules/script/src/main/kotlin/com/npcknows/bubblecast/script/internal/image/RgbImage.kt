package com.npcknows.bubblecast.script.internal.image

import com.npcknows.bubblecast.shared.PageImage
import kotlin.math.floor

/** Картинка как массив пикселей 0xRRGGBB — без Android, чтобы работать и в тестах на компьютере. */
internal class RgbImage(val width: Int, val height: Int, val pixels: IntArray) {
    init {
        require(pixels.size == width * height) { "пикселей ${pixels.size}, ждали ${width * height}" }
    }

    fun crop(x0: Int, y0: Int, x1: Int, y1: Int): RgbImage {
        val l = x0.coerceIn(0, width - 1)
        val t = y0.coerceIn(0, height - 1)
        val w = (x1.coerceIn(l + 1, width)) - l
        val h = (y1.coerceIn(t + 1, height)) - t
        return RgbImage(w, h, IntArray(w * h) { pixels[(t + it / w) * width + l + it % w] })
    }

    /** Билинейное изменение размера. */
    fun resize(w: Int, h: Int): RgbImage {
        val sx = width.toFloat() / w
        val sy = height.toFloat() / h
        val out = IntArray(w * h)
        for (y in 0 until h) {
            val fy = ((y + 0.5f) * sy - 0.5f).coerceIn(0f, (height - 1).toFloat())
            val y0 = floor(fy).toInt()
            val y1 = minOf(y0 + 1, height - 1)
            val dy = fy - y0
            for (x in 0 until w) {
                val fx = ((x + 0.5f) * sx - 0.5f).coerceIn(0f, (width - 1).toFloat())
                val x0 = floor(fx).toInt()
                val x1 = minOf(x0 + 1, width - 1)
                val dx = fx - x0
                var rgb = 0
                for (shift in intArrayOf(16, 8, 0)) {
                    fun c(px: Int) = (px shr shift) and 0xFF
                    val top = c(pixels[y0 * width + x0]) * (1 - dx) + c(pixels[y0 * width + x1]) * dx
                    val bottom = c(pixels[y1 * width + x0]) * (1 - dx) + c(pixels[y1 * width + x1]) * dx
                    rgb = rgb or ((top * (1 - dy) + bottom * dy + 0.5f).toInt().coerceIn(0, 255) shl shift)
                }
                out[y * w + x] = rgb
            }
        }
        return RgbImage(w, h, out)
    }

    /**
     * Тензор [3, h, w] для модели: (c/255 − mean) / std по каналам.
     * [bgr] — порядок каналов как у моделей PaddleOCR.
     */
    fun toChw(mean: FloatArray, std: FloatArray, bgr: Boolean): FloatArray {
        val plane = width * height
        val out = FloatArray(3 * plane)
        // Сдвиг байта канала в пикселе 0xRRGGBB для выходных каналов 0, 1, 2.
        val shifts = if (bgr) intArrayOf(0, 8, 16) else intArrayOf(16, 8, 0)
        for (c in 0 until 3) {
            val shift = shifts[c]
            val m = mean[c]
            val s = std[c]
            val base = c * plane
            for (i in 0 until plane) out[base + i] = (((pixels[i] shr shift) and 0xFF) / 255f - m) / s
        }
        return out
    }
}

/** Байты картинки страницы → пиксели. На телефоне — через Android, в тестах — через javax.imageio. */
internal fun interface ImageDecoder {
    fun decode(image: PageImage): RgbImage
}
