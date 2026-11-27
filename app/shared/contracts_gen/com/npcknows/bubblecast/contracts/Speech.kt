// СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/speech.schema.json. Руками не править.
package com.npcknows.bubblecast.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Запрос облачного синтеза одной реплики (Профи). */
@Serializable
data class SpeechRequest(
    /** Уже произносимый текст после text_prep. */
    @SerialName("text") val text: String,
    @SerialName("lang") val lang: Lang,
    @SerialName("voice_id") val voiceId: String,
    @SerialName("emotion") val emotion: Emotion,
    @SerialName("manner") val manner: Manner,
    @SerialName("intensity") val intensity: Double,
)

/** Готовый звук реплики. */
@Serializable
data class SpeechResult(
    @SerialName("audio_base64") val audioBase64: String,
    @SerialName("mime") val mime: String,
    @SerialName("duration_ms") val durationMs: Int,
)
