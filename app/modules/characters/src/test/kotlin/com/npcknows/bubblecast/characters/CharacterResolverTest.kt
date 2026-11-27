package com.npcknows.bubblecast.characters

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.shared.Layout
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PageImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CharacterResolverTest {
    private val resolver = CharacterResolver()
    private fun page(book: String, index: Int) = Page(book, index, "h$index", Layout.MANGA_RTL, PageImage(ByteArray(0), "image/png", 1, 1))
    private fun line(type: LineType, hint: String?) =
        Line("1", BBox(0.0, 0.0, 1.0, 1.0), "…", Lang.RU, type, hint, Emotion.NEUTRAL, Manner.NORMAL, 0.5)

    @Test fun sameHintIsSameCharacterAcrossPages() {
        val a = resolver.resolve(page("b", 1), line(LineType.DIALOGUE, "Рыжая девочка"))
        val b = resolver.resolve(page("b", 7), line(LineType.THOUGHT, "  рыжая   девочка "))
        assertEquals(a, b)
    }

    @Test fun booksDoNotShareCharacters() {
        val a = resolver.resolve(page("b1", 1), line(LineType.DIALOGUE, "герой"))
        val b = resolver.resolve(page("b2", 1), line(LineType.DIALOGUE, "герой"))
        assertNotEquals(a.id, b.id)
    }

    @Test fun narrationSfxAndMissingHint() {
        val p = page("b", 1)
        assertEquals("narrator", resolver.resolve(p, line(LineType.NARRATION, "кто угодно")).name)
        assertEquals("narrator", resolver.resolve(p, line(LineType.SFX, null)).name)
        assertEquals("someone", resolver.resolve(p, line(LineType.DIALOGUE, null)).name)
        assertEquals(listOf("narrator", "someone"), resolver.charactersOf("b").map { it.name })
    }
}
