package com.npcknows.bubblecast.text_prep

import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.shared.SpeakableText

/** Текст реплики → произносимый текст: ударения, ё, числа, звуки. RU и EN. */
interface TextPreparer {
    fun prepare(line: Line): SpeakableText
}

/** Подготовитель текста без ударений (пока модели не скачаны). */
fun TextPreparer(): TextPreparer = com.npcknows.bubblecast.text_prep.internal.DefaultTextPreparer()

/** Готовый подготовитель текста для сборки app/: с ударениями и ё для русского (модель грузится один раз). */
fun TextPreparer(models: com.npcknows.bubblecast.shared.ModelLocator): TextPreparer {
    val stress by lazy {
        com.npcknows.bubblecast.text_prep.internal.stress.RussianStress.open(models)
    }
    return com.npcknows.bubblecast.text_prep.internal.DefaultTextPreparer { text -> synchronized(stress) { stress.apply(text) } }
}
