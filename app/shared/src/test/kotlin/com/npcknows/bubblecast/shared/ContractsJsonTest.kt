package com.npcknows.bubblecast.shared

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Сгенерированные контракты пишутся в JSON так же, как их ждёт сервер (snake_case, значения enum). */
class ContractsJsonTest {
    @Test
    fun lineUsesServerNames() {
        val line = Line(
            id = "1", bbox = BBox(0.1, 0.2, 0.3, 0.4), text = "Привет!", lang = Lang.RU,
            type = LineType.DIALOGUE, speakerHint = "девочка", emotion = Emotion.HAPPY,
            manner = Manner.SHOUT, intensity = 0.8,
        )
        val json = Json.encodeToString(Line.serializer(), line)
        assertTrue(json.contains("\"speaker_hint\":\"девочка\""))
        assertTrue(json.contains("\"lang\":\"ru\""))
        assertEquals(line, Json.decodeFromString(Line.serializer(), json))
    }
}
