package com.npcknows.bubblecast.shared

import com.npcknows.bubblecast.contracts.Lang

@JvmInline
value class VoiceId(val value: String)

/** Откуда голос: встроенный в приложение, свой голос пользователя, облачный голос Профи. */
enum class VoiceOrigin { STANDARD, OWN, CLOUD }

data class Voice(
    val id: VoiceId,
    val name: String,
    val langs: Set<Lang>,
    val gender: Gender,
    val origin: VoiceOrigin,
)
