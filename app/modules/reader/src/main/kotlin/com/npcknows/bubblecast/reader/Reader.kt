package com.npcknows.bubblecast.reader

import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.VisibleArea
import kotlinx.coroutines.flow.StateFlow

/** Показ страниц на экране + что сейчас видно + прокрутка по команде. */
interface Reader {
    val visible: StateFlow<VisibleArea?>
    fun show(pages: List<Page>)
    fun scrollTo(area: VisibleArea)
}
