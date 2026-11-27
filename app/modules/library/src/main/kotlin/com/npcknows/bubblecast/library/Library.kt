package com.npcknows.bubblecast.library

/** Прочитанное → история и место, где остановился. */
interface Library {
    suspend fun saveProgress(bookId: String, pageIndex: Int)
    suspend fun progress(bookId: String): Int?
}
