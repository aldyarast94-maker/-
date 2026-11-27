package com.npcknows.bubblecast.pipeline

import com.npcknows.bubblecast.cast.Caster
import com.npcknows.bubblecast.characters.CharacterResolver
import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.PageScript
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.script.ScriptReader
import com.npcknows.bubblecast.shared.AudioClip
import com.npcknows.bubblecast.shared.Gender
import com.npcknows.bubblecast.shared.Layout
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PageImage
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId
import com.npcknows.bubblecast.shared.VoiceOrigin
import com.npcknows.bubblecast.text_prep.TextPreparer
import com.npcknows.bubblecast.tts.Synthesizer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** Настоящие characters, cast и text_prep; script и tts — подставные (модели появятся позже). */
class PagePipelineTest {
    private val page = Page("book", 3, "hash", Layout.COMIC_LTR, PageImage(ByteArray(0), "image/png", 1, 1))
    private fun line(id: String, text: String, type: LineType) =
        Line(id, BBox(0.0, 0.0, 1.0, 1.0), text, Lang.RU, type, null, Emotion.ANGRY, Manner.SHOUT, 0.9)

    private val fakeScript = object : ScriptReader {
        override suspend fun read(page: Page, tier: Tier) = PageScript(
            page.hash,
            listOf(
                line("1", "СТОЙ!!!", LineType.DIALOGUE),
                line("2", "КАФЕ «ЛУНА»", LineType.SIGN),
                line("3", "♥♥", LineType.DIALOGUE),
                line("4", "ПРОШЛО 3 ДНЯ.", LineType.NARRATION),
            ),
        )
    }
    private val spoken = mutableListOf<String>()
    private val fakeTts = object : Synthesizer {
        override suspend fun synthesize(
            text: SpeakableText, voice: Voice, emotion: Emotion, manner: Manner, intensity: Double, tier: Tier,
        ): AudioClip {
            spoken += "${voice.id.value}:${text.text}:$emotion"
            return AudioClip(ByteArray(1), "audio/wav", 100)
        }
    }
    private val narrator = Voice(VoiceId("ru-f"), "ru-f", setOf(Lang.RU), Gender.FEMALE, VoiceOrigin.STANDARD)

    @Test fun stepsRunInOrderAndSkipUnspeakable() = runBlocking {
        val pipeline = PagePipeline(
            fakeScript, CharacterResolver(), Caster({ listOf(narrator) }, { narrator.id }, { false }),
            TextPreparer(), fakeTts,
        )
        val lines = pipeline.process(page, Tier.BASIC).toList()
        assertEquals(listOf("1", "4"), lines.map { it.line.id })
        assertEquals(listOf("ru-f:Стой!:ANGRY", "ru-f:Прошло три дня.:ANGRY"), spoken)
        assertEquals(listOf("book/someone", "book/narrator"), lines.map { it.character.value })
    }
}
