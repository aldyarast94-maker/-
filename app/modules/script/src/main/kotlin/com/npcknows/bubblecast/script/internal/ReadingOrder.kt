package com.npcknows.bubblecast.script.internal

import com.npcknows.bubblecast.contracts.BBox
import com.npcknows.bubblecast.shared.Layout

/**
 * Порядок чтения найденных рамок без распознавания кадров.
 * Рамки собираются в «ряды» по высоте, ряды читаются сверху вниз, внутри ряда —
 * справа налево (манга) или слева направо (комикс). Вебтун — просто сверху вниз.
 * Чтобы манга с колонками читалась верно, позже добавим кадры; для первой версии этого хватает.
 */
internal object ReadingOrder {
    /** Доля перекрытия по высоте, при которой две рамки считаются одним рядом. */
    private const val SAME_ROW_OVERLAP = 0.5

    fun <T> sort(items: List<T>, layout: Layout, box: (T) -> BBox): List<T> {
        if (layout == Layout.WEBTOON) return items.sortedWith(compareBy({ box(it).y }, { box(it).x }))
        val horizontal: Comparator<T> =
            if (layout == Layout.MANGA_RTL) compareByDescending { box(it).x + box(it).w } else compareBy { box(it).x }
        return rows(items, box).flatMap { it.sortedWith(horizontal) }
    }

    /** Ряды сверху вниз: рамки, которые заметно перекрываются по высоте. Нужны и для склейки кусков строки. */
    fun <T> rows(items: List<T>, box: (T) -> BBox): List<List<T>> {
        val rows = mutableListOf<MutableList<T>>()
        for (item in items.sortedBy { box(it).y }) {
            val row = rows.lastOrNull()?.takeIf { r -> r.any { sameRow(box(it), box(item)) } }
            if (row != null) row += item else rows += mutableListOf(item)
        }
        return rows
    }

    private fun sameRow(a: BBox, b: BBox): Boolean {
        val overlap = minOf(a.y + a.h, b.y + b.h) - maxOf(a.y, b.y)
        return overlap >= SAME_ROW_OVERLAP * minOf(a.h, b.h)
    }
}
