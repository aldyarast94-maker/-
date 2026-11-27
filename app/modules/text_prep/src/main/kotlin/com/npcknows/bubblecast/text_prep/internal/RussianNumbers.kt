package com.npcknows.bubblecast.text_prep.internal

/** Количественные числительные до 999 999 999 в именительном падеже. */
internal object RussianNumbers {
    private val unitsMale = listOf("", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
    private val unitsFemale = listOf("", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
    private val teens = listOf(
        "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
        "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать",
    )
    private val tens = listOf("", "", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто")
    private val hundreds = listOf("", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот")

    fun words(n: Long): String {
        if (n == 0L) return "ноль"
        val parts = mutableListOf<String>()
        val millions = n / 1_000_000
        val thousands = n / 1_000 % 1_000
        val rest = n % 1_000
        if (millions > 0) parts += triple(millions, female = false) + plural(millions, "миллион", "миллиона", "миллионов")
        if (thousands > 0) parts += triple(thousands, female = true) + plural(thousands, "тысяча", "тысячи", "тысяч")
        if (rest > 0) parts += triple(rest, female = false)
        return parts.joinToString(" ")
    }

    /** Форма слова после числа: 1 процент, 2 процента, 5 процентов, 11 процентов. */
    fun plural(n: Long, one: String, few: String, many: String): String {
        val lastTwo = n % 100
        val last = n % 10
        return when {
            lastTwo in 11..14 -> many
            last == 1L -> one
            last in 2..4 -> few
            else -> many
        }
    }

    private fun triple(n: Long, female: Boolean): List<String> {
        val h = (n / 100).toInt()
        val t = (n / 10 % 10).toInt()
        val u = (n % 10).toInt()
        val units = if (female) unitsFemale else unitsMale
        return buildList {
            if (h > 0) add(hundreds[h])
            when {
                t == 1 -> add(teens[u])
                else -> {
                    if (t > 1) add(tens[t])
                    if (u > 0) add(units[u])
                }
            }
        }
    }
}
