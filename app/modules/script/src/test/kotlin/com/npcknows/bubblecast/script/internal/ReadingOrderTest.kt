package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.shared.Layout
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingOrderTest {
    // Страница 2×2: A — сверху слева, B — сверху справа (чуть ниже), C — снизу слева, D — снизу справа.
    private val boxes = mapOf(
        "A" to BBox(0.05, 0.10, 0.3, 0.2),
        "B" to BBox(0.60, 0.15, 0.3, 0.2),
        "C" to BBox(0.05, 0.60, 0.3, 0.2),
        "D" to BBox(0.60, 0.55, 0.3, 0.2),
    )

    private fun order(layout: Layout) = ReadingOrder.sort(boxes.keys.toList(), layout) { boxes.getValue(it) }.joinToString("")

    @Test fun comicReadsLeftToRightByRows() = assertEquals("ABCD", order(Layout.COMIC_LTR))

    @Test fun mangaReadsRightToLeftByRows() = assertEquals("BADC", order(Layout.MANGA_RTL))

    @Test fun webtoonReadsTopToBottom() = assertEquals("ABDC", order(Layout.WEBTOON))
}
