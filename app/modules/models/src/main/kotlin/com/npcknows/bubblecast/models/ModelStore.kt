package com.npcknows.bubblecast.models

import com.npcknows.bubblecast.shared.ModelFile
import com.npcknows.bubblecast.shared.ModelLocator
import kotlinx.coroutines.flow.Flow
import java.io.File

/** Сколько скачано из всего нужного. */
data class DownloadProgress(val doneBytes: Long, val totalBytes: Long, val currentFile: String?) {
    val fraction: Float get() = if (totalBytes == 0L) 1f else doneBytes.toFloat() / totalBytes
}

/**
 * Каталог моделей → файлы на телефоне: скачать при первом запуске с докачкой, проверить контрольную сумму,
 * показать прогресс. Остальные модули получают файлы только через [locator].
 */
interface ModelStore {
    val locator: ModelLocator

    /** Каких файлов из [files] ещё нет (или они повреждены). */
    fun missing(files: List<ModelFile>): List<ModelFile>

    /** Скачивает недостающие файлы; прерванное продолжается с места остановки. Ошибка сети — исключение. */
    fun download(files: List<ModelFile>): Flow<DownloadProgress>
}

/** Хранилище моделей в папке [root] (на телефоне — внутренняя память приложения). */
fun ModelStore(root: File): ModelStore = com.npcknows.bubblecast.models.internal.FileModelStore(root)
