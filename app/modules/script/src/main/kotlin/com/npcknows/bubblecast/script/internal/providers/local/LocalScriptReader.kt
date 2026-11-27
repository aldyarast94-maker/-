package com.npcknows.bubblecast.script.internal.providers.local

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.contracts.Line
import com.npcknows.bubblecast.contracts.PageScript
import com.npcknows.bubblecast.contracts.Tier
import com.npcknows.bubblecast.script.ScriptReader
import com.npcknows.bubblecast.script.internal.Heuristics
import com.npcknows.bubblecast.script.internal.ReadingOrder
import com.npcknows.bubblecast.script.internal.image.ImageDecoder
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.ModelLocator
import com.npcknows.bubblecast.shared.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Страница → сценарий на телефоне, без сети:
 * рамки текста → порядок чтения → строки в рамке → текст строк → язык, тип, простая эмоция.
 */
internal class LocalScriptReader(private val models: ModelLocator, private val decoder: ImageDecoder) : ScriptReader {
    private val lock = Mutex()
    private var parts: Triple<BubbleDetector, LineFinder, TextRecognizer>? = null

    override suspend fun read(page: Page, tier: Tier): PageScript = withContext(Dispatchers.Default) {
        lock.withLock {
            val (detector, lineFinder, recognizer) = parts ?: open().also { parts = it }
            val image = decoder.decode(page.image)
            val regions = ReadingOrder.sort(detector.detect(image), page.layout) { BBox(it.x0.toDouble(), it.y0.toDouble(), (it.x1 - it.x0).toDouble(), (it.y1 - it.y0).toDouble()) }
            val lines = regions.mapNotNull { region ->
                val crop = image.crop(region.x0.toInt(), region.y0.toInt(), region.x1.toInt() + 1, region.y1.toInt() + 1)
                val boxes = lineFinder.lines(crop).ifEmpty { listOf(BBox(0.0, 0.0, crop.width.toDouble(), crop.height.toDouble())) }
                val text = boxes.map { b ->
                    recognizer.recognize(crop.crop(b.x.toInt(), b.y.toInt(), (b.x + b.w).toInt() + 1, (b.y + b.h).toInt() + 1))
                }.filter { it.confidence >= MIN_CONFIDENCE && it.text.isNotBlank() }
                    .joinToString("\n") { it.text }
                if (text.isBlank()) null else region to text
            }
            PageScript(
                pageHash = page.hash,
                lines = lines.mapIndexed { i, (region, text) ->
                    val guess = Heuristics.guess(text)
                    Line(
                        id = "${page.index}-$i",
                        bbox = BBox(
                            region.x0.toDouble() / image.width, region.y0.toDouble() / image.height,
                            (region.x1 - region.x0).toDouble() / image.width, (region.y1 - region.y0).toDouble() / image.height,
                        ),
                        // Строки пузыря — через перевод строки: text_prep склеит переносы «ПРИ-/ВЕТ».
                        text = text,
                        lang = Heuristics.lang(text),
                        type = Heuristics.type(text, region.inBubble),
                        speakerHint = null,
                        emotion = guess.emotion,
                        manner = guess.manner,
                        intensity = guess.intensity,
                    )
                },
            )
        }
    }

    private fun open() = Triple(
        BubbleDetector(models.file(ModelCatalog.DETECTOR)),
        LineFinder(models.file(ModelCatalog.OCR_DETECTOR)),
        TextRecognizer(models.file(ModelCatalog.OCR_RECOGNIZER), models.file(ModelCatalog.OCR_DICTIONARY)),
    )

    private companion object {
        /** Ниже — скорее мусор (кусок рисунка, точка), чем текст. */
        const val MIN_CONFIDENCE = 0.6f
    }
}
