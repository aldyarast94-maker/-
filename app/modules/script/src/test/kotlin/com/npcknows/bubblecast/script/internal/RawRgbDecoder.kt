package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.script.internal.image.ImageDecoder
import com.npcknows.bubblecast.script.internal.image.RgbImage
import com.npcknows.bubblecast.shared.PageImage
import java.io.DataInputStream
import java.util.zip.GZIPInputStream

/** Декодер для тестов: читает пиксели из .rgb.gz (см. tools/make_test_pages.py). На телефоне — AndroidImageDecoder. */
internal object RawRgbDecoder : ImageDecoder {
    override fun decode(image: PageImage): RgbImage {
        DataInputStream(GZIPInputStream(image.bytes.inputStream())).use { input ->
            val w = input.readInt()
            val h = input.readInt()
            val rgb = ByteArray(w * h * 3).also(input::readFully)
            val pixels = IntArray(w * h) { i ->
                ((rgb[i * 3].toInt() and 0xFF) shl 16) or ((rgb[i * 3 + 1].toInt() and 0xFF) shl 8) or (rgb[i * 3 + 2].toInt() and 0xFF)
            }
            return RgbImage(w, h, pixels)
        }
    }
}
