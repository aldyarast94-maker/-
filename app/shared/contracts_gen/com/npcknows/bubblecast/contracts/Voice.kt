// СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/voice.schema.json. Руками не править.
package com.npcknows.bubblecast.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Запись голоса пользователя вместе с прочитанной фразой согласия. */
@Serializable
data class VoiceCloneRequest(
    @SerialName("sample_base64") val sampleBase64: String,
    @SerialName("sample_mime") val sampleMime: String,
    /** Фраза согласия, показанная на экране. */
    @SerialName("consent_text") val consentText: String,
    @SerialName("lang") val lang: Lang,
)

/** Созданный голос пользователя. */
@Serializable
data class VoiceProfile(
    @SerialName("voice_id") val voiceId: String,
    /** Время в ISO 8601. */
    @SerialName("created_at") val createdAt: String,
    /** Сколько пробных глав осталось на Базовом. */
    @SerialName("trial_left_chapters") val trialLeftChapters: Int,
)
