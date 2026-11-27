package com.npcknows.bubblecast.text_prep

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import org.junit.Assert.assertEquals
import org.junit.Test

class TextPreparerTest {
    private val prep = TextPreparer()

    private fun say(text: String, lang: Lang = Lang.RU, type: LineType = LineType.DIALOGUE) =
        prep.prepare(
            Line("1", BBox(0.0, 0.0, 1.0, 1.0), text, lang, type, null, Emotion.NEUTRAL, Manner.NORMAL, 0.5),
        ).text

    @Test fun capsBecomeSentenceCase() = assertEquals("Стой! Куда ты идёшь?", say("СТОЙ! КУДА ТЫ ИДЁШЬ?"))

    @Test fun ocrCaseMixupIsFixed() =
        assertEquals("Где Я? Что случилось? Не может быть!", say("ГДЕ Я? что случилось? НЕ МОЖЕТ БытЬ!"))

    @Test fun mixedDotsBecomeEllipsis() = assertEquals("Сломался…", say("СЛОМАЛСЯ.…."))

    @Test fun normalCaseIsKept() = assertEquals("Привет, Миша.", say("Привет, Миша."))

    @Test fun bubbleLineBreaksAreJoined() = assertEquals("Я не могу поверить", say("Я НЕ МО-\nГУ ПО-\nВЕРИТЬ"))

    @Test fun repeatsAreSqueezed() = assertEquals("Ааа! Что?! Ну…", say("АААААААА!!! ЧТО?!?! НУ......"))

    @Test fun symbolsAreDropped() = assertEquals("Люблю тебя", say("Люблю тебя ♥♥~"))

    @Test fun sfxIsReadable() = assertEquals("Ба-бах!", say("БА-БАХ!!!", type = LineType.SFX))

    @Test fun russianNumbers() {
        assertEquals("У меня двадцать одна тысяча монет", say("У меня 21000 монет"))
        assertEquals("сто процентов", say("100%"))
        assertEquals("три процента", say("3%"))
        assertEquals("одиннадцать процентов", say("11 %"))
        assertEquals("три запятая пять процента", say("3,5%"))
        assertEquals("два миллиона триста", say("2000300"))
    }

    @Test fun englishCapsAndNumbers() {
        assertEquals("I can't believe it! I have forty-two coins.", say("I CAN'T BELIEVE IT! I HAVE 42 COINS.", Lang.EN))
        assertEquals("one hundred percent", say("100%", Lang.EN))
        assertEquals("two million three hundred", say("2000300", Lang.EN))
    }
}
