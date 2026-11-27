package com.npcknows.bubblecast.api_client

import com.npcknows.bubblecast.contracts.Entitlement
import com.npcknows.bubblecast.contracts.PageScript
import com.npcknows.bubblecast.contracts.PurchaseVerifyRequest
import com.npcknows.bubblecast.contracts.Session
import com.npcknows.bubblecast.contracts.SignInRequest
import com.npcknows.bubblecast.contracts.SpeechRequest
import com.npcknows.bubblecast.contracts.SpeechResult
import com.npcknows.bubblecast.contracts.VisionRequest
import com.npcknows.bubblecast.contracts.VoiceCloneRequest
import com.npcknows.bubblecast.contracts.VoiceProfile

/** Запросы модулей → обмен с сервером по контрактам. Ключей API здесь нет — только токен пользователя. */
interface ApiClient {
    /** Токен ставит модуль account после входа. */
    var authToken: String?

    suspend fun signIn(request: SignInRequest): Session
    suspend fun verifyPurchase(request: PurchaseVerifyRequest): Entitlement
    suspend fun vision(request: VisionRequest): PageScript
    suspend fun speech(request: SpeechRequest): SpeechResult
    suspend fun cloneVoice(request: VoiceCloneRequest): VoiceProfile
    suspend fun deleteVoice(voiceId: String)
}
