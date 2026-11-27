package com.npcknows.bubblecast.text_prep.internal.stress

import java.io.DataInputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPInputStream

/** Файлы модели ударений: эмбеддинги n-грамм, словарь n-грамм, исключения, списки «не трогать». */
internal class StressData(
    /** [n, dim] построчно. */
    val embedding: FloatArray,
    val dim: Int,
    val ngrams: Map<String, Int>,
    val unk: Int,
    /** слово → (позиция ударной буквы, позиция «е», которая на самом деле «ё», или −1). */
    val exceptions: Map<String, Pair<Int, Int>>,
    val skipStress: Set<String>,
    val skipYo: Set<String>,
) {
    companion object {
        fun load(embedding: File, ngrams: File, exceptions: File, skipStress: File, skipYo: File): StressData {
            val (emb, dim) = readNpyFloat32(embedding)
            val grams = HashMap<String, Int>(200_000)
            lines(ngrams) { line ->
                val tab = line.lastIndexOf('\t')
                grams[line.substring(0, tab)] = line.substring(tab + 1).toInt()
            }
            val exc = HashMap<String, Pair<Int, Int>>(20_000)
            lines(exceptions) { line ->
                val (w, s, y) = line.split('\t')
                exc[w] = s.toInt() to y.toInt()
            }
            val stress = HashSet<String>().also { set -> lines(skipStress) { set += it } }
            val yo = HashSet<String>().also { set -> lines(skipYo) { set += it } }
            return StressData(emb, dim, grams, requireNotNull(grams["UNK"]) { "нет UNK в словаре n-грамм" }, exc, stress, yo)
        }

        private inline fun lines(file: File, each: (String) -> Unit) {
            GZIPInputStream(file.inputStream()).bufferedReader(Charsets.UTF_8).useLines { seq ->
                seq.forEach { if (it.isNotEmpty()) each(it) }
            }
        }

        /** Формат .npy версии 1: «\x93NUMPY», версия, длина заголовка (2 байта LE), заголовок, данные. */
        private fun readNpyFloat32(file: File): Pair<FloatArray, Int> {
            DataInputStream(file.inputStream().buffered()).use { input ->
                val magic = ByteArray(6).also(input::readFully)
                require(magic[0] == 0x93.toByte() && String(magic, 1, 5) == "NUMPY") { "не .npy: $file" }
                input.readUnsignedByte()
                input.readUnsignedByte()
                val headerLen = input.readUnsignedByte() or (input.readUnsignedByte() shl 8)
                val header = String(ByteArray(headerLen).also(input::readFully))
                require("'<f4'" in header && "'fortran_order': False" in header) { "ждали float32 по строкам: $header" }
                val (rows, cols) = Regex("""\((\d+),\s*(\d+)\)""").find(header)!!.destructured
                val count = rows.toInt() * cols.toInt()
                val bytes = ByteArray(count * 4).also(input::readFully)
                val out = FloatArray(count)
                ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(out)
                return out to cols.toInt()
            }
        }
    }
}
