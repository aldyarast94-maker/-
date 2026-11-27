package com.npcknows.bubblecast.account

import com.npcknows.bubblecast.contracts.Session
import kotlinx.coroutines.flow.StateFlow

/** Вход через Google → пользователь и его токен. */
interface Account {
    val session: StateFlow<Session?>
    suspend fun signIn(googleIdToken: String): Session
    suspend fun signOut()
}
