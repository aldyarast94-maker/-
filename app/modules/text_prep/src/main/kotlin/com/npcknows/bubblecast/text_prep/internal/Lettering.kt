package com.npcknows.bubblecast.text_prep.internal

import com.npcknows.bubblecast.contracts.Lang

/** Особенности комиксного леттеринга: переносы в пузыре, капс, «!!!», «Аааааа», значки. */
internal object Lettering {
    private val hyphenBreak = Regex("""(\p{L})-\s*\n\s*(\p{L})""")
    private val anyBreak = Regex("""\s*\n\s*""")
    private val unspeakable = Regex("""[♥♡♪♫★☆~*#_<>|\\^]""")
    private val stretchedLetter = Regex("""(\p{L})\1{3,}""", RegexOption.IGNORE_CASE)
    private val manyDots = Regex("""\.{3,}|…+""")
    private val manyBangs = Regex("""!{2,}""")
    private val manyQuestions = Regex("""\?{2,}""")
    private val mixedMarks = Regex("""[?!]*\?[?!]*![?!]*|[?!]*![?!]*\?[?!]*""")
    private val sentenceStart = Regex("""(^|[.!?…]\s+|^[—-]\s*)(\p{Ll})""")
    private val loneI = Regex("""\bi\b""")
    private val spaces = Regex("""\s+""")

    /** «ПРИ-\nВЕТ» → «ПРИВЕТ», остальные переносы строк — пробел. */
    fun joinBubbleLines(text: String): String =
        text.replace(hyphenBreak, "$1$2").replace(anyBreak, " ")

    fun dropUnspeakable(text: String): String = text.replace(unspeakable, " ")

    /** «Аааааа» → «Ааа», «!!!» → «!», «?!?!» → «?!», «....» → «…». */
    fun squeezeRepeats(text: String): String = text
        .replace(stretchedLetter) { it.value.take(3) }
        .replace(manyDots, "…")
        .replace(mixedMarks, "?!")
        .replace(manyBangs, "!")
        .replace(manyQuestions, "?")

    /**
     * Комиксы набирают капсом, а голос читает капс хуже (иногда по буквам).
     * Если букв в верхнем регистре почти все — переводим в обычный регистр предложений.
     */
    fun uncap(text: String, lang: Lang): String {
        val letters = text.filter(Char::isLetter)
        if (letters.length < 2 || letters.count(Char::isUpperCase) < letters.length * 0.8) return text
        val lower = text.lowercase()
        val sentences = lower.replace(sentenceStart) { m -> m.groupValues[1] + m.groupValues[2].uppercase() }
        return if (lang == Lang.EN) sentences.replace(loneI, "I") else sentences
    }

    fun collapseSpaces(text: String): String = text.replace(spaces, " ").trim()
}
