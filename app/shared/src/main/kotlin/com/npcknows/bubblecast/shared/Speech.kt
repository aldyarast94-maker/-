package com.npcknows.bubblecast.shared

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.contracts.Line

/** Текст, готовый к синтезу: ударения, числа словами, звуки расписаны. */
data class SpeakableText(val text: String, val lang: Lang)

/** Готовый звук. */
class AudioClip(val bytes: ByteArray, val mime: String, val durationMs: Int)

/** Реплика, которую можно играть: что сказано, кем, каким голосом и звук. */
data class PlayableLine(
    val bookId: String,
    val pageIndex: Int,
    val line: Line,
    val character: CharacterId,
    val voice: VoiceId,
    val audio: AudioClip,
)

/** «Я веду» — звук догоняет пользователя; «Веди меня» — экран идёт за звуком. */
enum class NarrationMode { FOLLOW_ME, LEAD_ME }
