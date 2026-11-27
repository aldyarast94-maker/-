package com.npcknows.bubblecast.script.internal.image

import android.graphics.BitmapFactory
import com.npcknows.bubblecast.shared.PageImage

internal object AndroidImageDecoder : ImageDecoder {
    override fun decode(image: PageImage): RgbImage {
        val bitmap = requireNotNull(BitmapFactory.decodeByteArray(image.bytes, 0, image.bytes.size)) { "не картинка: ${image.mime}" }
        try {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            for (i in pixels.indices) pixels[i] = pixels[i] and 0xFFFFFF
            return RgbImage(bitmap.width, bitmap.height, pixels)
        } finally {
            bitmap.recycle()
        }
    }
}
