package com.npcknows.bubblecast.script

import com.npcknows.bubblecast.contracts.PageScript
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.Page

/** Картинка страницы → сценарий реплик. Поставщики: local (на телефоне) и cloud (сервер, Профи). */
interface ScriptReader {
    suspend fun read(page: Page, tier: Tier): PageScript
}
