package com.npcknows.bubblecast.shared

@JvmInline
value class CharacterId(val value: String)

enum class Gender { MALE, FEMALE, UNKNOWN }

enum class AgeGroup { CHILD, TEEN, ADULT, ELDER, UNKNOWN }

/** Персонаж книги: один и тот же через все страницы и главы. Чтец (текст автора) — тоже персонаж. */
data class Character(
    val id: CharacterId,
    val bookId: String,
    val name: String,
    val gender: Gender,
    val age: AgeGroup,
)
