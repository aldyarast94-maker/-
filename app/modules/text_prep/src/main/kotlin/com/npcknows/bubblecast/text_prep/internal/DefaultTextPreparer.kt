package com.npcknows.bubblecast.text_prep.internal

import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.text_prep.TextPreparer

/**
 * Порядок важен: сначала склеить строки пузыря, потом чистить знаки,
 * потом снять капс, в конце — числа словами (им нужен уже чистый текст).
 */
internal class DefaultTextPreparer : TextPreparer {
    override fun prepare(line: Line): SpeakableText {
        val text = line.text
            .let(Lettering::joinBubbleLines)
            .let(Lettering::dropUnspeakable)
            .let(Lettering::squeezeRepeats)
            .let { Lettering.uncap(it, line.lang) }
            .let { Numbers.spellOut(it, line.lang) }
            .let(Lettering::collapseSpaces)
        return SpeakableText(text, line.lang)
    }
}
