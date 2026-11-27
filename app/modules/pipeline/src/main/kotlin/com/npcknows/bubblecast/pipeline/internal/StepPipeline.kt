package com.npcknows.bubblecast.pipeline.internal

import com.npcknows.bubblecast.cast.Caster
import com.npcknows.bubblecast.characters.CharacterResolver
import com.npcknows.bubblecast.contracts.LineType
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.pipeline.PagePipeline
import com.npcknows.bubblecast.script.ScriptReader
import com.npcknows.bubblecast.shared.Page
import com.npcknows.bubblecast.shared.PlayableLine
import com.npcknows.bubblecast.text_prep.TextPreparer
import com.npcknows.bubblecast.tts.Synthesizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** script → characters → cast → text_prep → tts. Реплики отдаются по одной, как только готов звук. */
internal class StepPipeline(
    private val script: ScriptReader,
    private val characters: CharacterResolver,
    private val cast: Caster,
    private val textPrep: TextPreparer,
    private val tts: Synthesizer,
) : PagePipeline {
    override fun process(page: Page, tier: Tier): Flow<PlayableLine> = flow {
        for (line in script.read(page, tier).lines) {
            if (line.type == LineType.SIGN) continue
            val speakable = textPrep.prepare(line)
            if (speakable.text.isBlank()) continue
            val character = characters.resolve(page, line)
            val voice = cast.voiceFor(character, line.lang)
            val audio = tts.synthesize(speakable, voice, line.emotion, line.manner, line.intensity, tier)
            emit(PlayableLine(page.bookId, page.index, line, character.id, voice.id, audio))
        }
    }
}
