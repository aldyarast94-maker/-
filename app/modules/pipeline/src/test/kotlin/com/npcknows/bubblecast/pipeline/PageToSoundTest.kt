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
import com.npcknows.bubblecast.shared.Layout
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PageImage
import com.npcknows.bubblecast.shared.testing.TestModels
import com.npcknows.bubblecast.text_prep.TextPreparer
import com.npcknows.bubblecast.tts.StandardVoices
import com.npcknows.bubblecast.tts.Synthesizer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Сценарий → звук через настоящие text_prep, characters, cast и tts (Supertonic-3; нужен BUBBLECAST_MODELS).
 * Сценарий — то, что script распознал на тестовых страницах (см. LocalScriptReaderTest), с его ошибками регистра.
 * Звук пишется в build/page-samples/ — послушать.
 */
class PageToSoundTest {
    private val out = File("build/page-samples").apply { mkdirs() }

    @Before fun models() = TestModels.assumeAvailable(*ModelCatalog.ALL.filter { it.path.startsWith("supertonic") }.toTypedArray())

    private fun line(i: Int, text: String, lang: Lang, emotion: Emotion = Emotion.NEUTRAL, manner: Manner = Manner.NORMAL) =
        Line("$i", BBox(0.0, 0.1 * i, 0.5, 0.1), text, lang, LineType.DIALOGUE, null, emotion, manner, 0.6)

    private val recognized = mapOf(
        "comic_ru_en" to listOf(
            line(0, "ПРИВЕТ! ТЫ\nОПЯТЬ ОПОЗДАЛ?", Lang.RU),
            line(1, "ПРОСТИ, АВТОБУС\nСЛОМАЛСЯ…", Lang.RU),
            line(2, "WHERE IS\nTHE DRAGON?", Lang.EN),
            line(3, "IT FLEW AWAY\nLAST NIGHT!", Lang.EN),
        ),
        "webtoon_ru" to listOf(
            line(0, "ГДЕ Я?\nчто случилось?", Lang.RU),
            line(1, "ТЫ ПРОСПАЛ\nТРИ ДНЯ.", Lang.RU),
            line(2, "НЕ МОЖЕТ\nБытЬ!", Lang.RU, manner = Manner.SHOUT),
        ),
    )

    @Test fun pagesBecomeSpeech() = runBlocking {
        val tts = Synthesizer(TestModels.locator)
        val script = object : ScriptReader {
            override suspend fun read(page: Page, tier: Tier) = PageScript(page.hash, recognized.getValue(page.hash))
        }
        val pipeline = PagePipeline(
            script, CharacterResolver(), Caster(tts::voices, { StandardVoices.FEMALE }, { false }), TextPreparer(), tts,
        )
        for ((name, lines) in recognized) {
            val page = Page(name, 0, name, Layout.COMIC_LTR, PageImage(ByteArray(0), "image/png", 0, 0))
            val played = pipeline.process(page, Tier.BASIC).toList()
            assertEquals(lines.map { it.id }, played.map { it.line.id })
            assertTrue(played.all { it.voice == StandardVoices.FEMALE && it.audio.durationMs > 500 })
            played.forEachIndexed { i, p -> File(out, "${name}_$i.wav").writeBytes(p.audio.bytes) }
        }
    }
}
