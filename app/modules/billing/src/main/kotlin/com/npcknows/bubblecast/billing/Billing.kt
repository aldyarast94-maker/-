package com.npcknows.bubblecast.billing

import com.npcknows.bubblecast.contracts.Entitlement
import kotlinx.coroutines.flow.StateFlow

/** Покупка в Google Play → уровень (Базовый/Профи) и остаток лимита. */
interface Billing {
    val entitlement: StateFlow<Entitlement>
    suspend fun verify(productId: String, purchaseToken: String): Entitlement
}
