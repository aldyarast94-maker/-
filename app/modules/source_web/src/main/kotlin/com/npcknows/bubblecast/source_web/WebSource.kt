package com.npcknows.bubblecast.source_web

import com.npcknows.bubblecast.shared.PageSource

/** Глава, открытая во встроенном браузере → её страницы (картинки, которые сайт уже загрузил). */
interface WebSource {
    fun open(url: String): PageSource
}
