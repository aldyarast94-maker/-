package com.npcknows.bubblecast.reading_session

import com.npcknows.bubblecast.shared.NarrationMode
import com.npcknows.bubblecast.shared.PageSource
import kotlinx.coroutines.flow.StateFlow

enum class SessionState { IDLE, PREPARING, PLAYING, PAUSED, FINISHED }

/** Источник + позиция чтения → команды pipeline, reader и narration. Только связывает. */
interface ReadingSession {
    val state: StateFlow<SessionState>
    fun start(source: PageSource, mode: NarrationMode)
    fun pause()
    fun resume()
    fun stop()
}
