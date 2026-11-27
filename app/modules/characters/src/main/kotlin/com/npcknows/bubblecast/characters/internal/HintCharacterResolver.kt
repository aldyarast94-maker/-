package com.npcknows.bubblecast.characters.internal

import com.npcknows.bubblecast.characters.CharacterResolver
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.shared.AgeGroup
import com.npcknows.bubblecast.shared.Character
import com.npcknows.bubblecast.shared.CharacterId
import com.npcknows.bubblecast.shared.Gender
import com.npcknows.bubblecast.shared.Page

/**
 * Персонаж по подсказке со страницы (speaker_hint). Одинаковая подсказка в одной книге — один персонаж.
 * Текст автора и звуки — всегда чтец. Нет подсказки (Базовый уровень) — «кто-то», один на книгу.
 * Хранится в памяти; сохранение между запусками — позже, вместе с library.
 */
internal class HintCharacterResolver : CharacterResolver {
    private val books = mutableMapOf<String, LinkedHashMap<String, Character>>()

    @Synchronized
    override fun resolve(page: Page, line: Line): Character {
        val key = when (line.type) {
            LineType.NARRATION, LineType.SFX, LineType.SIGN -> NARRATOR
            LineType.DIALOGUE, LineType.THOUGHT -> line.speakerHint?.let(::normalize)?.ifEmpty { null } ?: UNKNOWN
        }
        val cast = books.getOrPut(page.bookId) { LinkedHashMap() }
        return cast.getOrPut(key) {
            Character(
                id = CharacterId("${page.bookId}/$key"),
                bookId = page.bookId,
                name = key,
                gender = Gender.UNKNOWN,
                age = AgeGroup.UNKNOWN,
            )
        }
    }

    @Synchronized
    override fun charactersOf(bookId: String): List<Character> = books[bookId]?.values?.toList().orEmpty()

    private fun normalize(hint: String): String = hint.trim().lowercase().replace(Regex("""\s+"""), " ")

    private companion object {
        const val NARRATOR = "narrator"
        const val UNKNOWN = "someone"
    }
}
