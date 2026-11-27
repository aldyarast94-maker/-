// СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/script.schema.json. Руками не править.
package com.npcknows.bubblecast.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Одна реплика на странице в порядке чтения. */
@Serializable
data class Line(
    @SerialName("id") val id: String,
    @SerialName("bbox") val bbox: BBox,
    @SerialName("text") val text: String,
    @SerialName("lang") val lang: Lang,
    @SerialName("type") val type: LineType,
    /** Кто говорит, как это видно на странице (не стабильный id). */
    @SerialName("speaker_hint") val speakerHint: String? = null,
    @SerialName("emotion") val emotion: Emotion,
    @SerialName("manner") val manner: Manner,
    /** Сила эмоции 0..1. */
    @SerialName("intensity") val intensity: Double,
)

/** Все реплики страницы в порядке чтения. */
@Serializable
data class PageScript(
    @SerialName("page_hash") val pageHash: String,
    @SerialName("lines") val lines: List<Line>,
)

/** Запрос облачного понимания страницы (Профи). */
@Serializable
data class VisionRequest(
    @SerialName("page_hash") val pageHash: String,
    @SerialName("image_base64") val imageBase64: String,
    @SerialName("image_mime") val imageMime: String,
)
