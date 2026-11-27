package com.npcknows.bubblecast.shared.testing

import com.npcknows.bubblecast.shared.ModelFile
import com.npcknows.bubblecast.shared.ModelLocator
import org.junit.Assume.assumeTrue
import java.io.File

/**
 * Модели для тестов лежат в папке из переменной BUBBLECAST_MODELS (те же пути, что в ModelCatalog).
 * Нет папки или файла — тест пропускается, а не падает: в CI моделей (сотни МБ) нет.
 */
object TestModels {
    private val root: File? = System.getenv("BUBBLECAST_MODELS")?.let(::File)

    val locator = ModelLocator { model -> File(requireNotNull(root) { "BUBBLECAST_MODELS не задан" }, model.path) }

    fun assumeAvailable(vararg models: ModelFile) {
        assumeTrue("нет BUBBLECAST_MODELS — тест с моделями пропущен", root != null)
        val missing = models.filterNot { locator.file(it).isFile }.map { it.path }
        assumeTrue("нет файлов моделей: $missing", missing.isEmpty())
    }
}
