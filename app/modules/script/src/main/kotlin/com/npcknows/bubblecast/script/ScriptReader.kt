package com.npcknows.bubblecast.script

import com.npcknows.bubblecast.contracts.PageScript
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.shared.Page

/** Картинка страницы → сценарий реплик. Поставщики: local (на телефоне) и cloud (сервер, Профи). */
interface ScriptReader {
    suspend fun read(page: Page, tier: Tier): PageScript
}

/** Готовый чтец страниц для сборки app/. Облачный поставщик (Профи) подключится на этапе 7. */
fun ScriptReader(models: com.npcknows.bubblecast.shared.ModelLocator): ScriptReader =
    com.npcknows.bubblecast.script.internal.providers.local.LocalScriptReader(
        models, com.npcknows.bubblecast.script.internal.image.AndroidImageDecoder,
    )
