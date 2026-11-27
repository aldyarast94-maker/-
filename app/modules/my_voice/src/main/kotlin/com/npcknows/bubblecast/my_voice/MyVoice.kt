package com.npcknows.bubblecast.my_voice

import com.npcknows.bubblecast.contracts.Lang
import com.npcknows.bubblecast.shared.Voice
import com.npcknows.bubblecast.shared.VoiceId

/** Запись голоса пользователя + согласие → свой голос. Только свой голос; удаляется одной кнопкой. */
interface MyVoice {
    /** Фраза согласия, которую пользователь читает вслух перед записью. */
    fun consentText(lang: Lang): String
    suspend fun create(sample: ByteArray, mime: String, lang: Lang): Voice
    suspend fun delete(voice: VoiceId)
}
