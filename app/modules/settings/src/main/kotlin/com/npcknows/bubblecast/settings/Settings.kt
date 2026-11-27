package com.npcknows.bubblecast.settings

import com.npcknows.bubblecast.shared.NarrationMode
import com.npcknows.bubblecast.shared.VoiceId
import kotlinx.coroutines.flow.StateFlow

/** Действия пользователя → сохранённые настройки. */
interface Settings {
    val narratorVoice: StateFlow<VoiceId?>
    val mode: StateFlow<NarrationMode>
    suspend fun setNarratorVoice(voice: VoiceId)
    suspend fun setMode(mode: NarrationMode)
}
