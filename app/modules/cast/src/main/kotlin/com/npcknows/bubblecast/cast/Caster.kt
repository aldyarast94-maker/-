package com.npcknows.bubblecast.cast

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.CharacterId
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId

/** Персонаж → голос. Выбор пользователя важнее подбора. */
interface Caster {
    fun voiceFor(character: Character, lang: Lang): Voice
    fun assign(character: CharacterId, voice: Voice)
    fun available(lang: Lang): List<Voice>
}

/**
 * Готовый подборщик голосов для сборки app/.
 * @param catalog голоса, которые умеет tts сейчас (подключает сборка).
 * @param narrator голос чтеца из настроек (выбран при первом запуске).
 * @param perCharacter true — у каждого персонажа свой голос (Профи); false — всё читает чтец (Базовый).
 */
fun Caster(
    catalog: () -> List<Voice>,
    narrator: () -> VoiceId?,
    perCharacter: () -> Boolean,
): Caster = com.npcknows.bubblecast.cast.internal.DefaultCaster(catalog, narrator, perCharacter)
