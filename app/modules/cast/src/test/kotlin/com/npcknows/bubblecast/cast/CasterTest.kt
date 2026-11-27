package com.npcknows.bubblecast.cast

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.shared.AgeGroup
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.CharacterId
import com.npcknows.bubblecast.shared.Gender
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId
import com.npcknows.bubblecast.shared.VoiceOrigin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CasterTest {
    private fun voice(id: String, lang: Lang, gender: Gender) = Voice(VoiceId(id), id, setOf(lang), gender, VoiceOrigin.STANDARD)
    private val ruMale = voice("ru-m", Lang.RU, Gender.MALE)
    private val ruFemale = voice("ru-f", Lang.RU, Gender.FEMALE)
    private val enMale = voice("en-m", Lang.EN, Gender.MALE)
    private val enFemale = voice("en-f", Lang.EN, Gender.FEMALE)
    private val catalog = listOf(ruMale, ruFemale, enMale, enFemale)
    private fun hero(id: String, gender: Gender = Gender.UNKNOWN) = Character(CharacterId(id), "b", id, gender, AgeGroup.UNKNOWN)

    @Test fun basicTierReadsEverythingWithNarrator() {
        val caster = Caster({ catalog }, { ruFemale.id }, { false })
        assertEquals(ruFemale, caster.voiceFor(hero("a", Gender.MALE), Lang.RU))
        assertEquals(ruFemale, caster.voiceFor(hero("b"), Lang.RU))
    }

    @Test fun narratorSwitchesToSameGenderForOtherLanguage() {
        val caster = Caster({ catalog }, { ruFemale.id }, { false })
        assertEquals(enFemale, caster.voiceFor(hero("a"), Lang.EN))
    }

    @Test fun userChoiceWins() {
        val caster = Caster({ catalog }, { ruFemale.id }, { false })
        caster.assign(CharacterId("a"), ruMale)
        assertEquals(ruMale, caster.voiceFor(hero("a"), Lang.RU))
    }

    @Test fun proGivesDifferentVoicesAndKeepsThem() {
        val caster = Caster({ catalog }, { null }, { true })
        val a = caster.voiceFor(hero("a"), Lang.RU)
        val b = caster.voiceFor(hero("b"), Lang.RU)
        assertNotEquals(a, b)
        assertEquals(a, caster.voiceFor(hero("a"), Lang.RU))
        assertEquals(ruMale, Caster({ catalog }, { null }, { true }).voiceFor(hero("m", Gender.MALE), Lang.RU))
    }
}
