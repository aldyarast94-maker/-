package com.npcknows.bubblecast.characters

import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.Page

/** «Говорящий» со страницы → один и тот же персонаж через всю книгу. */
interface CharacterResolver {
    fun resolve(page: Page, line: Line): Character
    fun charactersOf(bookId: String): List<Character>
}

/** Готовый определитель персонажей для сборки app/. */
fun CharacterResolver(): CharacterResolver = com.npcknows.bubblecast.characters.internal.HintCharacterResolver()
