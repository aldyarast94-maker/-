package com.npcknows.bubblecast.overlay

import kotlinx.coroutines.flow.Flow

enum class OverlayAction { START, PAUSE, RESUME, STOP }

/** Кнопки поверх других приложений → действия пользователя. */
interface Overlay {
    val actions: Flow<OverlayAction>
    fun show()
    fun hide()
}
