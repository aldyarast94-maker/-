package com.npcknows.bubblecast.text_prep.internal.stress

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.npcknows.bubblecast.shared.ModelCatalog
import com.npcknows.bubblecast.shared.ModelLocator
import java.io.File
import java.nio.FloatBuffer
import kotlin.math.exp

/**
 * Ударения и «ё» для русского текста: знак ударения U+0301 после ударной гласной, «е» → «ё», где надо.
 * Перенос silero-stress (github.com/snakers4/silero-stress, MIT) по ONNX-экспорту stressonnx
 * (github.com/TigreGotico/stressonnx, Apache-2.0): n-граммы слова → среднее эмбеддингов → сеть →
 * номер ударной гласной и решение «е → ё»; словарь исключений важнее сети.
 *
 * Отличия от оригинала (для голоса так чище): над «ё» и в словах с одной гласной знак не ставим —
 * ударение там и так очевидно.
 */
internal class RussianStress(model: File, private val data: StressData) : AutoCloseable {
    private val env = OrtEnvironment.getEnvironment()
    private val session = env.createSession(model.path, OrtSession.SessionOptions())

    private data class Token(val raw: String, val clean: String, val predict: Boolean)
    private data class Prediction(val stress: Int, val stressP: Float, val yo: Int, val yoP: Float)

    fun apply(text: String): String {
        val tokens = tokenize(text)
        val predictions = predict(tokens)
        val marks = mutableListOf<Int>()
        val yo = mutableListOf<Int>()
        var pos = 0
        tokens.forEachIndexed { i, t ->
            val start = pos
            pos += t.raw.length
            if (!t.predict || STRESS in t.raw) return@forEachIndexed
            decode(t, predictions[i] ?: return@forEachIndexed, start, marks, yo)
        }
        return render(text, marks, yo)
    }

    private fun decode(t: Token, p: Prediction, start: Int, marks: MutableList<Int>, yo: MutableList<Int>) {
        val lower = t.raw.lowercase().takeIf { it.length == t.raw.length } ?: return
        if ('ё' in lower) return // ё всегда ударная — знак не нужен
        val vowels = lower.indices.filter { lower[it] in VOWELS }
        if (vowels.size <= 1) return // одна гласная — ударение очевидно
        data.exceptions[t.clean]?.let { (s, y) ->
            val yoApplied = y in lower.indices && lower[y] == 'е'
            if (yoApplied) yo += start + y
            if (s in lower.indices && lower[s] in VOWELS && !(yoApplied && y == s)) marks += start + s
            return
        }
        val stressPos = vowels.getOrNull(p.stress) ?: return
        val ye = lower.indices.filter { lower[it] == 'е' }
        val yoPos = ye.getOrNull(p.yo - 1)
        if (p.yoP > 0.5f && t.clean !in data.skipYo && yoPos == stressPos) {
            yo += start + stressPos
            return // стала «ё» — знак не нужен
        }
        if (p.stressP > 0.5f && t.clean !in data.skipStress) marks += start + stressPos
    }

    private fun predict(tokens: List<Token>): Array<Prediction?> {
        val out = arrayOfNulls<Prediction>(tokens.size)
        val idx = tokens.indices.filter { tokens[it].predict }
        if (idx.isEmpty()) return out
        val pooled = FloatArray(idx.size * data.dim)
        idx.forEachIndexed { row, i -> pool(tokens[i].clean, pooled, row * data.dim) }
        OnnxTensor.createTensor(env, FloatBuffer.wrap(pooled), longArrayOf(idx.size.toLong(), data.dim.toLong())).use { x ->
            session.run(mapOf("pooled" to x)).use { r ->
                val s = r[0] as OnnxTensor
                val y = r[1] as OnnxTensor
                val sk = s.info.shape[1].toInt()
                val yk = y.info.shape[1].toInt()
                val sb = s.floatBuffer
                val yb = y.floatBuffer
                idx.forEachIndexed { row, i ->
                    val (sArg, sP) = argmaxSoftmax(FloatArray(sk) { sb[row * sk + it] })
                    val (yArg, yP) = argmaxSoftmax(FloatArray(yk) { yb[row * yk + it] })
                    out[i] = Prediction(sArg, sP, yArg, yP)
                }
            }
        }
        return out
    }

    /** Среднее эмбеддингов всех n-грамм «<слово>» (как fastText); незнакомое слово — эмбеддинг UNK. */
    private fun pool(word: String, out: FloatArray, offset: Int) {
        val t = "<$word>"
        var count = 0
        for (n in 1..word.length + 2) {
            for (j in 0..t.length - n) {
                val id = data.ngrams[t.substring(j, j + n)] ?: continue
                add(id, out, offset)
                count++
            }
        }
        if (count == 0) { add(data.unk, out, offset); count = 1 }
        for (k in 0 until data.dim) out[offset + k] /= count
    }

    private fun add(id: Int, out: FloatArray, offset: Int) {
        val base = id * data.dim
        for (k in 0 until data.dim) out[offset + k] += data.embedding[base + k]
    }

    private fun argmaxSoftmax(logits: FloatArray): Pair<Int, Float> {
        val max = logits.max()
        val e = logits.map { exp((it - max).toDouble()) }
        val arg = e.indices.maxBy { e[it] }
        return arg to (e[arg] / e.sum()).toFloat()
    }

    /** Слова — между разделителями; части через дефис предсказываются отдельно, частицы «-то», «-ка»… — без ударения. */
    private fun tokenize(text: String): List<Token> {
        val out = mutableListOf<Token>()
        var last = 0
        fun word(w: String) {
            val parts = w.split('-')
            parts.forEachIndexed { i, part ->
                val raw = if (i < parts.size - 1) "$part-" else part
                val clean = raw.lowercase().replace(NOT_RU, "")
                val clitic = i == parts.size - 1 && parts.size > 1 && part.lowercase() in CLITICS
                out += Token(raw, clean, clean.isNotEmpty() && !clitic)
            }
        }
        for (m in SPLIT.findAll(text)) {
            if (m.range.first > last) word(text.substring(last, m.range.first))
            out += Token(m.value, "", false)
            last = m.range.last + 1
        }
        if (last < text.length) word(text.substring(last))
        return out
    }

    private fun render(text: String, marks: List<Int>, yo: List<Int>): String {
        val chars = text.toMutableList()
        for (o in yo) if (o in chars.indices) chars[o] = if (chars[o] == 'Е') 'Ё' else if (chars[o] == 'е') 'ё' else chars[o]
        for (o in marks.sortedDescending()) if (o in chars.indices) chars.add(o + 1, STRESS)
        return chars.joinToString("")
    }

    override fun close() = session.close()

    companion object {
        fun open(models: ModelLocator) = RussianStress(
            models.file(ModelCatalog.STRESS_MODEL),
            StressData.load(
                models.file(ModelCatalog.STRESS_EMBEDDING), models.file(ModelCatalog.STRESS_NGRAMS),
                models.file(ModelCatalog.STRESS_EXCEPTIONS), models.file(ModelCatalog.STRESS_SKIP_STRESS),
                models.file(ModelCatalog.STRESS_SKIP_YO),
            ),
        )

        private const val STRESS = '\u0301'
        private const val VOWELS = "аоуыэиеяёю"
        private val SPLIT = Regex("""[\s.,!?;:<>=()/\\«»„“”"…—–%№*@\[\]{}0-9]+""")
        private val NOT_RU = Regex("""[^А-Яа-яёЁ]""")
        private val CLITICS = setOf("то", "нибудь", "либо", "таки", "ка")
    }
}
