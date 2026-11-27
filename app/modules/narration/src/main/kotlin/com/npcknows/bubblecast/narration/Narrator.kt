package com.npcknows.bubblecast.narration

import com.npcknows.bubblecast.shared.NarrationMode
import com.npcknows.bubblecast.shared.PlayableLine
import com.npcknows.bubblecast.shared.VisibleArea
import kotlinx.coroutines.flow.StateFlow

/** Готовые реплики + видимая область → что и когда играть, какую реплику подсветить. */
interface Narrator {
    val current: StateFlow<PlayableLine?>
    fun setMode(mode: NarrationMode)
    fun enqueue(lines: List<PlayableLine>)
    fun onVisible(area: VisibleArea)
    fun pause()
    fun resume()
}
