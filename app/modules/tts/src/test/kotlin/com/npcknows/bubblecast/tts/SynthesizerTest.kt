package com.npcknows.bubblecast.tts

import com.npcknows.bubblecast.contracts.Emotion
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Manner
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.SpeakableText
import com.npcknows.bubblecast.shared.testing.TestModels
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/** С настоящей моделью Supertonic-3 (нужен BUBBLECAST_MODELS). Звук пишется в build/tts-samples/ — послушать. */
class SynthesizerTest {
    private val tts by lazy { Synthesizer(TestModels.locator) }
    private val out = File("build/tts-samples").apply { mkdirs() }

    @Before fun models() = TestModels.assumeAvailable(*ModelCatalog.ALL.filter { it.path.startsWith("supertonic") }.toTypedArray())

    private fun voice(id: com.npcknows.bubblecast.shared.VoiceId) = tts.voices().first { it.id == id }

    private fun say(name: String, text: String, lang: Lang, voice: com.npcknows.bubblecast.shared.VoiceId, emotion: Emotion = Emotion.NEUTRAL, manner: Manner = Manner.NORMAL) =
        runBlocking { tts.synthesize(SpeakableText(text, lang), voice(voice), emotion, manner, 0.8, Tier.BASIC) }
            .also { File(out, "$name.wav").writeBytes(it.bytes) }

    @Test fun tenVoicesBothLanguages() {
        assertEquals(10, tts.voices().size)
        assertTrue(tts.voices().all { it.langs == setOf(Lang.RU, Lang.EN) })
    }

    @Test fun russianAndEnglishSoundLikeSpeech() {
        val ru = say("ru_female", "Привет! Ты опять опоздал?", Lang.RU, StandardVoices.FEMALE)
        val en = say("en_male", "It flew away last night!", Lang.EN, StandardVoices.MALE)
        for (clip in listOf(ru, en)) {
            assertEquals("audio/wav", clip.mime)
            assertTrue("длительность ${clip.durationMs} мс", clip.durationMs in 1000..5000)
        }
    }

    @Test fun sameTextSameSound() {
        val a = say("repeat_a", "Ёжик идёт через лес.", Lang.RU, StandardVoices.MALE)
        val b = say("repeat_b", "Ёжик идёт через лес.", Lang.RU, StandardVoices.MALE)
        assertArrayEquals(a.bytes, b.bytes)
    }

    @Test fun angerIsFasterThanSadness() {
        val angry = say("ru_angry", "Я больше не могу ждать!", Lang.RU, StandardVoices.MALE, Emotion.ANGRY, Manner.SHOUT)
        val sad = say("ru_sad", "Я больше не могу ждать!", Lang.RU, StandardVoices.MALE, Emotion.SAD)
        assertTrue("злость ${angry.durationMs} мс, грусть ${sad.durationMs} мс", angry.durationMs < sad.durationMs)
    }
}
