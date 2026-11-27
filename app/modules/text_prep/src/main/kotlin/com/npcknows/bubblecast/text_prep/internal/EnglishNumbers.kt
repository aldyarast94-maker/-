package com.npcknows.bubblecast.text_prep.internal

/** Cardinal numbers up to 999 999 999. */
internal object EnglishNumbers {
    private val small = listOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
    )
    private val tens = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

    fun words(n: Long): String {
        if (n < 20) return small[n.toInt()]
        val parts = mutableListOf<String>()
        val millions = n / 1_000_000
        val thousands = n / 1_000 % 1_000
        val rest = n % 1_000
        if (millions > 0) parts += "${triple(millions)} million"
        if (thousands > 0) parts += "${triple(thousands)} thousand"
        if (rest > 0) parts += triple(rest)
        return parts.joinToString(" ")
    }

    private fun triple(n: Long): String {
        val h = (n / 100).toInt()
        val r = (n % 100).toInt()
        val parts = mutableListOf<String>()
        if (h > 0) parts += "${small[h]} hundred"
        when {
            r in 1..19 -> parts += small[r]
            r >= 20 -> parts += if (r % 10 == 0) tens[r / 10] else "${tens[r / 10]}-${small[r % 10]}"
        }
        return parts.joinToString(" ")
    }
}
