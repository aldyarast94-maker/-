package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner

/** Подача реплики, которую можно угадать по самому тексту. */
internal data class Guess(val emotion: Emotion, val manner: Manner, val intensity: Double)

/**
 * Простые правила Базового уровня — без ИИ, только по тексту и месту реплики.
 * Настоящие эмоции (по лицам и смыслу) — в Профи, на сервере.
 */
internal object Heuristics {
    private val cyrillic = Regex("""\p{IsCyrillic}""")
    private val latin = Regex("""\p{IsLatin}""")
    private val words = Regex("""\p{L}+""")

    /** Язык по буквам: больше кириллицы — русский. */
    fun lang(text: String): Lang =
        if (cyrillic.findAll(text).count() >= latin.findAll(text).count()) Lang.RU else Lang.EN

    /**
     * Текст в пузыре — речь. Вне пузыря: короткое с «!» — звук, иначе — текст автора.
     * Мысли от речи без формы пузыря не отличить — это Профи.
     */
    fun type(text: String, inBubble: Boolean): LineType = when {
        inBubble -> LineType.DIALOGUE
        words.findAll(text).count() <= 2 && text.trimEnd().endsWith("!") -> LineType.SFX
        else -> LineType.NARRATION
    }

    fun guess(text: String): Guess {
        val t = text.trim()
        val bangs = t.count { it == '!' }
        return when {
            "?!" in t || "!?" in t -> Guess(Emotion.SURPRISED, Manner.SHOUT, 0.7)
            bangs >= 2 -> Guess(Emotion.NEUTRAL, Manner.SHOUT, 0.8)
            bangs == 1 -> Guess(Emotion.NEUTRAL, Manner.NORMAL, 0.6)
            t.endsWith("…") || t.endsWith("...") -> Guess(Emotion.NEUTRAL, Manner.NORMAL, 0.3)
            else -> Guess(Emotion.NEUTRAL, Manner.NORMAL, 0.5)
        }
    }
}
