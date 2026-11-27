package com.npcknows.bubblecast.voices

/** Персонажи книги → экран выбора голоса каждому. Экран — Compose, появится на этапе 7. */
interface VoicesScreen {
    fun open(bookId: String)
}
