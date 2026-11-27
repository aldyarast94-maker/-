package com.npcknows.bubblecast.shared

import kotlinx.coroutines.flow.Flow

/** Как читать страницы: манга — справа налево, комикс — слева направо, вебтун — лентой сверху вниз. */
enum class Layout { MANGA_RTL, COMIC_LTR, WEBTOON }

/** Картинка страницы. Сравнение по содержимому не нужно — для этого есть Page.hash. */
class PageImage(val bytes: ByteArray, val mime: String, val width: Int, val height: Int)

/** Одна страница (или кусок ленты вебтуна) одной книги. */
data class Page(
    val bookId: String,
    val index: Int,
    /** Хеш картинки: ключ кэша и связь с ответами сервера. */
    val hash: String,
    val layout: Layout,
    val image: PageImage,
)

/** Откуда пришла книга. */
enum class SourceKind { FILES, WEB, SCREEN }

/** Любой источник страниц. Конкретные источники знает только сборка app/. */
interface PageSource {
    val kind: SourceKind

    /** Можно ли читать без рук: источник сам отдаёт следующие страницы, не дожидаясь пользователя. */
    val canLeadHandsFree: Boolean

    /** Страницы по мере появления. */
    fun pages(): Flow<Page>
}
