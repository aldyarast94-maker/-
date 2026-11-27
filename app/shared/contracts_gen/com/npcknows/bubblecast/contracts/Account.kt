// СГЕНЕРИРОВАНО tools/gen_contracts.py из contracts/account.schema.json. Руками не править.
package com.npcknows.bubblecast.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Вход через Google. */
@Serializable
data class SignInRequest(
    @SerialName("google_id_token") val googleIdToken: String,
)

/** Ответ на вход: токен для запросов и текущий уровень. */
@Serializable
data class Session(
    @SerialName("user_id") val userId: String,
    @SerialName("token") val token: String,
    @SerialName("entitlement") val entitlement: Entitlement,
)

/** Чек покупки из Google Play для проверки на сервере. */
@Serializable
data class PurchaseVerifyRequest(
    @SerialName("product_id") val productId: String,
    @SerialName("purchase_token") val purchaseToken: String,
)

/** Что доступно пользователю сейчас. */
@Serializable
data class Entitlement(
    @SerialName("tier") val tier: Tier,
    /** Остаток облачных страниц в периоде. */
    @SerialName("pages_left") val pagesLeft: Int,
)
