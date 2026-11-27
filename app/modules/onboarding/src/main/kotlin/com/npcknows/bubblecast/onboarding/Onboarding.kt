package com.npcknows.bubblecast.onboarding

import com.npcknows.bubblecast.shared.Voice

/** Первый запуск → выбранный голос чтеца (стандартный мужской / женский / свой). */
interface Onboarding {
    val isDone: Boolean
    suspend fun complete(narrator: Voice)
}
