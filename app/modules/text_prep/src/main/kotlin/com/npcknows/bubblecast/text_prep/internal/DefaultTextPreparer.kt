package com.npcknows.bubblecast.text_prep.internal

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.text_prep.TextPreparer

/**
 * Порядок важен: сначала склеить строки пузыря, потом чистить знаки,
 * потом снять капс, числа словами (им нужен уже чистый текст), в конце — ударения (и для чисел-слов тоже).
 */
internal class DefaultTextPreparer(
    /** Ударения и ё для русского; null — без них (модели ещё не скачаны, тесты без моделей). */
    private val russianStress: ((String) -> String)? = null,
) : TextPreparer {
    override fun prepare(line: Line): SpeakableText {
        val text = line.text
            .let(Lettering::joinBubbleLines)
            .let(Lettering::dropUnspeakable)
            .let(Lettering::squeezeRepeats)
            .let { Lettering.uncap(it, line.lang) }
            .let { Numbers.spellOut(it, line.lang) }
            .let(Lettering::collapseSpaces)
            .let { if (line.lang == Lang.RU && russianStress != null) russianStress.invoke(it) else it }
        return SpeakableText(text, line.lang)
    }
}
