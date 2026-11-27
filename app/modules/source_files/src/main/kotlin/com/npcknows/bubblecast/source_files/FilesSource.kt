package com.npcknows.bubblecast.source_files

import android.net.Uri
import com.npcknows.bubblecast.shared.PageSource

/** Файлы пользователя (CBZ/ZIP/PDF/картинки) → страницы главы. */
interface FilesSource {
    fun open(uri: Uri): PageSource
}
