package com.npcknows.bubblecast.text_prep.internal

import com.npcknows.bubblecast.contracts.Lang

/** Числа словами: «5 монет» → «пять монет», «100%» → «сто процентов». */
internal object Numbers {
    private val number = Regex("""(\d+)(?:[.,](\d+))?(\s?%)?""")

    fun spellOut(text: String, lang: Lang): String = text.replace(number) { m ->
        val whole = m.groupValues[1]
        val fraction = m.groupValues[2]
        val percent = m.groupValues[3].isNotEmpty()
        // Слишком длинные числа (номера телефонов, коды) читаем по цифрам.
        if (whole.length > 9) return@replace whole.map { words(it.digitToInt().toLong(), lang) }.joinToString(" ")
        val n = whole.toLong()
        buildString {
            append(words(n, lang))
            if (fraction.isNotEmpty()) append(if (lang == Lang.RU) " запятая " else " point ").append(words(fraction.toLong(), lang))
            // С дробной частью: «3,5 процента» — та же форма, что после «двух».
            if (percent) append(" ").append(percentWord(if (fraction.isEmpty()) n else 2, lang))
        }
    }

    fun words(n: Long, lang: Lang): String = when (lang) {
        Lang.RU -> RussianNumbers.words(n)
        Lang.EN -> EnglishNumbers.words(n)
    }

    private fun percentWord(n: Long, lang: Lang): String = when (lang) {
        Lang.RU -> RussianNumbers.plural(n, "процент", "процента", "процентов")
        Lang.EN -> "percent"
    }
}
