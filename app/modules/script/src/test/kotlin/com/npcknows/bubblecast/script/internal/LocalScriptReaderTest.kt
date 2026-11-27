package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.script.internal.providers.local.LocalScriptReader
import com.npcknows.bubblecast.shared.Layout
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PageImage
import com.npcknows.bubblecast.shared.testing.TestModels
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Страницы из tools/make_test_pages.py с настоящими моделями (нужен BUBBLECAST_MODELS). */
class LocalScriptReaderTest {
    private val reader by lazy { LocalScriptReader(TestModels.locator, RawRgbDecoder) }

    @Before fun models() = TestModels.assumeAvailable(
        ModelCatalog.DETECTOR, ModelCatalog.OCR_DETECTOR, ModelCatalog.OCR_RECOGNIZER, ModelCatalog.OCR_DICTIONARY,
    )

    private fun read(name: String): Pair<List<String>, com.npcknows.bubblecast.contracts.PageScript> {
        val meta = Json.parseToJsonElement(resource("$name.json").decodeToString()).jsonObject
        val expected = meta.getValue("expected").jsonArray.map { it.jsonPrimitive.content }
        val layout = Layout.valueOf(meta.getValue("layout").jsonPrimitive.content)
        val bytes = resource("$name.rgb.gz")
        val page = Page("book", 0, name, layout, PageImage(bytes, "application/x-rgb-gzip", 0, 0))
        return expected to runBlocking { reader.read(page, Tier.BASIC) }
    }

    private fun resource(path: String) = requireNotNull(javaClass.getResourceAsStream("/pages/$path")).readBytes()

    @Test fun comicPageRussianAndEnglishInReadingOrder() {
        val (expected, script) = read("comic_ru_en")
        assertMatches(expected, script.lines.map { it.text })
        assertEquals(listOf(Lang.RU, Lang.RU, Lang.EN, Lang.EN), script.lines.map { it.lang })
        assertTrue(script.lines.all { it.type == LineType.DIALOGUE })
    }

    @Test fun webtoonStripIsCutAndReadTopToBottom() {
        val (expected, script) = read("webtoon_ru")
        assertMatches(expected, script.lines.map { it.text })
    }

    /**
     * Распознавание не обязано быть буква в букву: до 10% ошибок по символам в реплике.
     * Регистр не сравниваем — модель путает одинаковые по виду с/С, т/Т (это чинит text_prep), «...» = «…».
     */
    private fun assertMatches(expected: List<String>, actual: List<String>) {
        fun norm(s: String) = s.replace('\n', ' ').replace("...", "…").uppercase()
        val got = actual.map(::norm)
        println("распознано: $actual")
        assertEquals("число реплик: $got", expected.size, got.size)
        expected.map(::norm).zip(got).forEach { (e, a) ->
            val errors = distance(e, a).toDouble() / e.length
            assertTrue("«$a» вместо «$e» (ошибок ${"%.0f".format(errors * 100)}%)", errors <= 0.1)
        }
    }

    private fun distance(a: String, b: String): Int {
        var prev = IntArray(b.length + 1) { it }
        for (i in 1..a.length) {
            val cur = IntArray(b.length + 1)
            cur[0] = i
            for (j in 1..b.length) cur[j] = minOf(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1)
            prev = cur
        }
        return prev[b.length]
    }
}
