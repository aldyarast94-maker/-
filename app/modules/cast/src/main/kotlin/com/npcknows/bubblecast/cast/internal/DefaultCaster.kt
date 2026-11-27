package com.npcknows.bubblecast.cast.internal

import com.npcknows.bubblecast.cast.Caster
import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.CharacterId
import com.npcknows.bubblecast.shared.Gender
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId
import com.npcknows.bubblecast.shared.VoiceOrigin

/**
 * Порядок выбора голоса:
 * 1. голос, который пользователь назначил персонажу (если говорит на этом языке);
 * 2. Базовый уровень — голос чтеца; если чтец не говорит на языке страницы — стандартный голос того же пола;
 * 3. Профи — голос по полу персонажа, разные персонажи по возможности получают разные голоса.
 */
internal class DefaultCaster(
    private val catalog: () -> List<Voice>,
    private val narrator: () -> VoiceId?,
    private val perCharacter: () -> Boolean,
) : Caster {
    private val assigned = mutableMapOf<CharacterId, Voice>()
    private val auto = mutableMapOf<Pair<CharacterId, Lang>, Voice>()

    @Synchronized
    override fun voiceFor(character: Character, lang: Lang): Voice {
        assigned[character.id]?.takeIf { lang in it.langs }?.let { return it }
        val voices = available(lang)
        check(voices.isNotEmpty()) { "нет голосов для языка $lang" }
        if (!perCharacter()) return narratorFor(lang, voices)
        return auto.getOrPut(character.id to lang) { pickFor(character, lang, voices) }
    }

    @Synchronized
    override fun assign(character: CharacterId, voice: Voice) {
        assigned[character] = voice
    }

    override fun available(lang: Lang): List<Voice> = catalog().filter { lang in it.langs }

    private fun narratorFor(lang: Lang, voices: List<Voice>): Voice {
        val chosen = narrator()?.let { id -> catalog().firstOrNull { it.id == id } }
            ?: return voices.first()
        if (lang in chosen.langs) return chosen
        return voices.firstOrNull { it.origin == VoiceOrigin.STANDARD && it.gender == chosen.gender } ?: voices.first()
    }

    private fun pickFor(character: Character, lang: Lang, voices: List<Voice>): Voice {
        val sameGender = voices.filter { character.gender == Gender.UNKNOWN || it.gender == character.gender }
            .ifEmpty { voices }
        val used = auto.filterKeys { it.second == lang }.values.toSet()
        return sameGender.firstOrNull { it !in used } ?: sameGender[auto.size % sameGender.size]
    }
}
