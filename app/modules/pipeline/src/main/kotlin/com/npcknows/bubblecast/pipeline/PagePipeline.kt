package com.npcknows.bubblecast.pipeline

import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PlayableLine
import kotlinx.coroutines.flow.Flow

/**
 * Страница → готовые к проигрыванию реплики.
 * Только вызывает по порядку: script → characters → cast → text_prep → tts. Своей логики нет.
 */
interface PagePipeline {
    fun process(page: Page, tier: Tier): Flow<PlayableLine>
}
