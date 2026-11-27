package com.npcknows.bubblecast.text_prep

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.testing.TestModels
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Ударения с настоящей моделью (нужен BUBBLECAST_MODELS). Ожидаемое — ответ оригинала silero-stress (stressonnx),
 * но без знака над «ё» и в словах с одной гласной — так задумано (см. RussianStress).
 */
class StressTest {
    private val prep by lazy { TextPreparer(TestModels.locator) }

    @Before fun models() = TestModels.assumeAvailable(*ModelCatalog.ALL.filter { it.path.startsWith("stress/") }.toTypedArray())

    private fun say(text: String, lang: Lang = Lang.RU) = prep.prepare(
        Line("1", BBox(0.0, 0.0, 1.0, 1.0), text, lang, LineType.DIALOGUE, null, Emotion.NEUTRAL, Manner.NORMAL, 0.5),
    ).text

    @Test fun matchesSileroStress() {
        assertEquals("Приве́т! Ты опя́ть опозда́л?", say("Привет! Ты опять опоздал?"))
        assertEquals("Прости́, авто́бус слома́лся…", say("Прости, автобус сломался…"))
        assertEquals("Мы пришли́ домо́й и легли́ спать.", say("Мы пришли домой и легли спать."))
    }

    @Test fun restoresYoWithoutExtraMarks() =
        assertEquals("Ёжик идёт че́рез лес. Все хорошо́.", say("Ежик идет через лес. Все хорошо."))

    @Test fun cliticsAndUnsureHomographsStayUnmarked() {
        assertEquals("Кто-то стучи́т в дверь.", say("Кто-то стучит в дверь."))
        assertEquals("Ёлка стоит в углу́.", say("Ёлка стоит в углу."))
    }

    @Test fun comicCapsAndNumbersGetStress() = assertEquals("Прошло́ три дня.", say("ПРОШЛО 3 ДНЯ."))

    @Test fun englishIsUntouched() = assertEquals("Where is the dragon?", say("WHERE IS THE DRAGON?", Lang.EN))
}
