package com.npcknows.bubblecast.source_screen

import android.content.Intent
import com.npcknows.bubblecast.shared.PageSource

/** Разрешение на захват экрана → кадры экрана как страницы. Без чтения без рук. */
interface ScreenSource {
    /** resultCode и data — ответ системного окна разрешения MediaProjection. */
    fun start(resultCode: Int, data: Intent): PageSource
    fun stop()
}
