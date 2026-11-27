package com.npcknows.bubblecast.cast

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.CharacterId
import com.npcknows.bubblecast.shared.Voice

/** Персонаж → голос. Выбор пользователя важнее подбора. */
interface Caster {
    fun voiceFor(character: Character, lang: Lang): Voice
    fun assign(character: CharacterId, voice: Voice)
    fun available(lang: Lang): List<Voice>
}
