package com.npcknows.bubblecast.tts.internal.providers.local

import java.text.Normalizer

/**
 * Текст → номера символов для Supertonic-3.
 * Перенос UnicodeProcessor из примера Supertone (github.com/supertone-inc/supertonic, MIT, © 2025 Supertone Inc.).
 * Большую часть чистки текста уже сделал text_prep; здесь — только то, что нужно самой модели.
 */
internal class SupertonicText(private val indexer: LongArray) {
    private val replacements = listOf(
        "–" to "-", "‑" to "-", "—" to "-", "_" to " ",
        "“" to "\"", "”" to "\"", "‘" to "'", "’" to "'", "´" to "'", "`" to "'",
        "[" to " ", "]" to " ", "|" to " ", "/" to " ", "#" to " ", "→" to " ", "←" to " ",
    )
    private val spaceBeforeMark = Regex(""" ([,.!?;:'])""")
    private val endsWithMark = Regex(""".*[.!?;:,'"“”‘’)\]}…。」』】〉》›»]$""")
    private val spaces = Regex("""\s+""")

    /** [lang] — код языка модели: "ru", "en". */
    fun ids(text: String, lang: String): LongArray {
        var t = Normalizer.normalize(text, Normalizer.Form.NFKD)
        t = t.codePoints().filter { !isEmoji(it) }.toArray().let { String(it, 0, it.size) }
        for ((from, to) in replacements) t = t.replace(from, to)
        t = t.replace(Regex("""[♥☆♡©\\]"""), "")
            .replace(spaceBeforeMark, "$1")
            .replace(Regex("\"{2,}"), "\"").replace(Regex("'{2,}"), "'")
            .replace(spaces, " ").trim()
        if (!endsWithMark.matches(t)) t += "."
        t = "<$lang>$t</$lang>"
        return t.codePoints().toArray().map { cp -> indexer.getOrElse(cp) { -1L } }
            .filter { it >= 0 }.toLongArray()
    }

    private fun isEmoji(cp: Int) = cp in 0x1F600..0x1F64F || cp in 0x1F300..0x1F5FF || cp in 0x1F680..0x1F6FF ||
        cp in 0x1F700..0x1FAFF || cp in 0x2600..0x27BF || cp in 0x1F1E6..0x1F1FF
}
