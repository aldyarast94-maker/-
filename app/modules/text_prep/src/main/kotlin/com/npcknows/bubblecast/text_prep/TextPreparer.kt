package com.npcknows.bubblecast.text_prep

import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.shared.SpeakableText

/** Текст реплики → произносимый текст: ударения, ё, числа, звуки. RU и EN. */
interface TextPreparer {
    fun prepare(line: Line): SpeakableText
}
