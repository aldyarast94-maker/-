package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import org.junit.Assert.assertEquals
import org.junit.Test

class HeuristicsTest {
    @Test fun language() {
        assertEquals(Lang.RU, Heuristics.lang("ПРИВЕТ! OK"))
        assertEquals(Lang.EN, Heuristics.lang("WHERE IS THE DRAGON?"))
    }

    @Test fun lineType() {
        assertEquals(LineType.DIALOGUE, Heuristics.type("БАХ!", inBubble = true))
        assertEquals(LineType.SFX, Heuristics.type("БА-БАХ!", inBubble = false))
        assertEquals(LineType.NARRATION, Heuristics.type("Прошло три дня.", inBubble = false))
    }

    @Test fun deliveryFromPunctuation() {
        assertEquals(Emotion.SURPRISED, Heuristics.guess("ЧТО?!").emotion)
        assertEquals(Manner.SHOUT, Heuristics.guess("СТОЙ!!").manner)
        assertEquals(Manner.NORMAL, Heuristics.guess("Привет.").manner)
    }
}
