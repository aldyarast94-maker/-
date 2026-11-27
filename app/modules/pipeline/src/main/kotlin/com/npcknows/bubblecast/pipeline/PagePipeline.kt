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

/** Готовый порядок обработки для сборки app/: соединяет переданные шаги. */
fun PagePipeline(
    script: com.npcknows.bubblecast.script.ScriptReader,
    characters: com.npcknows.bubblecast.characters.CharacterResolver,
    cast: com.npcknows.bubblecast.cast.Caster,
    textPrep: com.npcknows.bubblecast.text_prep.TextPreparer,
    tts: com.npcknows.bubblecast.tts.Synthesizer,
): PagePipeline = com.npcknows.bubblecast.pipeline.internal.StepPipeline(script, characters, cast, textPrep, tts)
