package com.npcknows.bubblecast.shared

import com.npcknows.bubblecast.contracts.BBox

/** Что пользователь видит сейчас: страница и видимая её часть (доли 0..1). */
data class VisibleArea(val bookId: String, val pageIndex: Int, val box: BBox)
