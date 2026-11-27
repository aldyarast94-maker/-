// СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/common.schema.json. Руками не править.
package com.npcknows.bubblecast.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Язык реплики. Пока два. */
@Serializable
enum class Lang { @SerialName("ru") RU, @SerialName("en") EN }

/** Тип реплики: речь, мысль, текст автора, звук, надпись на фоне. */
@Serializable
enum class LineType { @SerialName("dialogue") DIALOGUE, @SerialName("thought") THOUGHT, @SerialName("narration") NARRATION, @SerialName("sfx") SFX, @SerialName("sign") SIGN }

/** Эмоция, с которой читать реплику. */
@Serializable
enum class Emotion { @SerialName("neutral") NEUTRAL, @SerialName("happy") HAPPY, @SerialName("sad") SAD, @SerialName("angry") ANGRY, @SerialName("scared") SCARED, @SerialName("surprised") SURPRISED, @SerialName("disgusted") DISGUSTED, @SerialName("tender") TENDER, @SerialName("sarcastic") SARCASTIC }

/** Манера подачи: обычно, шёпотом, криком. */
@Serializable
enum class Manner { @SerialName("normal") NORMAL, @SerialName("whisper") WHISPER, @SerialName("shout") SHOUT }

/** Уровень: Базовый (на телефоне) или Профи (подписка). */
@Serializable
enum class Tier { @SerialName("basic") BASIC, @SerialName("pro") PRO }

/** Рамка на странице в долях от ширины и высоты (0..1). */
@Serializable
data class BBox(
    @SerialName("x") val x: Double,
    @SerialName("y") val y: Double,
    @SerialName("w") val w: Double,
    @SerialName("h") val h: Double,
)
