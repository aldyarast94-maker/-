package com.npcknows.bubblecast.models.internal

import com.npcknows.bubblecast.models.DownloadProgress
import com.npcknows.bubblecast.models.ModelStore
import com.npcknows.bubblecast.shared.ModelFile
import com.npcknows.bubblecast.shared.ModelLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Файлы лежат по путям каталога внутри [root]. Скачивание идёт в «<файл>.part» с докачкой (Range),
 * после проверки sha256 файл переименовывается на место. Готовый файл проверяется один раз и помечается «.ok»,
 * чтобы не пересчитывать сотни мегабайт при каждом запуске.
 */
internal class FileModelStore(private val root: File) : ModelStore {
    override val locator = ModelLocator { File(root, it.path) }

    override fun missing(files: List<ModelFile>): List<ModelFile> = files.filterNot(::isReady)

    override fun download(files: List<ModelFile>): Flow<DownloadProgress> = flow {
        val total = files.sumOf { it.sizeBytes }
        var done = files.filter(::isReady).sumOf { it.sizeBytes }
        emit(DownloadProgress(done, total, null))
        for (model in files.filterNot(::isReady)) {
            val before = done
            fetch(model) { bytesOfThisFile -> done = before + bytesOfThisFile; emit(DownloadProgress(done, total, model.path)) }
            done = before + model.sizeBytes
        }
        emit(DownloadProgress(total, total, null))
    }.flowOn(Dispatchers.IO)

    private fun isReady(model: ModelFile): Boolean {
        val file = locator.file(model)
        if (!file.isFile || file.length() != model.sizeBytes) return false
        val mark = File(file.path + ".ok")
        if (mark.isFile && mark.readText() == model.sha256) return true
        if (sha256(file) != model.sha256) return false
        mark.writeText(model.sha256)
        return true
    }

    private suspend fun fetch(model: ModelFile, progress: suspend (Long) -> Unit) {
        val target = locator.file(model)
        target.parentFile?.mkdirs()
        val part = File(target.path + ".part")
        if (part.length() > model.sizeBytes) part.delete()
        var tries = 0
        var restarts = 0
        while (true) {
            tries++
            try {
                readInto(model, part, progress)
            } catch (e: IOException) {
                // Связь оборвалась — кусок остаётся, следующая попытка докачает.
                if (tries >= MAX_TRIES) throw IOException("${model.path}: связь оборвалась (${e.message})", e)
                continue
            }
            if (part.length() < model.sizeBytes) {
                if (tries >= MAX_TRIES) throw IOException("${model.path}: связь оборвалась")
                continue
            }
            if (part.length() == model.sizeBytes && sha256(part) == model.sha256) {
                target.delete()
                if (!part.renameTo(target)) throw IOException("${model.path}: не удалось переименовать")
                File(target.path + ".ok").writeText(model.sha256)
                progress(model.sizeBytes)
                return
            }
            // Битый файл: один раз качаем заново целиком.
            part.delete()
            if (++restarts > 1) throw IOException("${model.path}: контрольная сумма не совпала")
        }
    }

    /** Дописывает в [part] с того места, где он кончается (Range), или пишет заново, если сервер докачку не умеет. */
    private suspend fun readInto(model: ModelFile, part: File, progress: suspend (Long) -> Unit) {
        val have = part.length()
        val conn = (URL(model.url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            if (have > 0) setRequestProperty("Range", "bytes=$have-")
        }
        try {
            val code = conn.responseCode
            val append = code == HttpURLConnection.HTTP_PARTIAL
            if (code != HttpURLConnection.HTTP_OK && !append) throw IOException("ответ сервера $code")
            var written = if (append) have else 0L
            conn.inputStream.use { input ->
                FileOutputStream(part, append).use { out ->
                    val buf = ByteArray(64 * 1024)
                    var lastReport = 0L
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val n = input.read(buf)
                        if (n < 0) break
                        out.write(buf, 0, n)
                        written += n
                        if (written - lastReport >= REPORT_EVERY) { progress(written); lastReport = written }
                    }
                }
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buf = ByteArray(1 shl 16)
            while (true) {
                val n = input.read(buf)
                if (n < 0) break
                digest.update(buf, 0, n)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val REPORT_EVERY = 1L shl 20
        const val MAX_TRIES = 3
    }
}
