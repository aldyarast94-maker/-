package com.npcknows.bubblecast.models

import com.npcknows.bubblecast.shared.ModelFile
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.net.InetSocketAddress
import java.security.MessageDigest
import kotlin.random.Random

class ModelStoreTest {
    @get:Rule val tmp = TemporaryFolder()

    private val content = Random(1).nextBytes(3_000_000)
    private val ranges = mutableListOf<String?>()
    private var mode = "ok" // ok | drop-once | corrupt
    private var dropped = false
    private val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        createContext("/m.bin") { ex ->
            val range = ex.requestHeaders.getFirst("Range")
            synchronized(ranges) { ranges += range }
            val from = range?.removePrefix("bytes=")?.removeSuffix("-")?.toInt() ?: 0
            val body = when (mode) {
                "corrupt" -> ByteArray(content.size)
                else -> content
            }.copyOfRange(from, content.size)
            ex.sendResponseHeaders(if (from > 0) 206 else 200, body.size.toLong())
            ex.responseBody.use { out ->
                if (mode == "drop-once" && !dropped) {
                    dropped = true
                    out.write(body, 0, body.size / 2)
                    out.flush()
                    ex.close() // обрыв связи посреди файла
                    return@createContext
                }
                out.write(body)
            }
        }
        start()
    }

    private val model = ModelFile(
        "dir/m.bin", "http://127.0.0.1:${server.address.port}/m.bin",
        MessageDigest.getInstance("SHA-256").digest(content).joinToString("") { "%02x".format(it) },
        content.size.toLong(), "test",
    )

    @After fun stop() = server.stop(0)

    @Test fun downloadsVerifiesAndReportsProgress() = runBlocking {
        val store = ModelStore(tmp.root)
        assertEquals(listOf(model), store.missing(listOf(model)))
        val progress = store.download(listOf(model)).toList()
        assertEquals(1f, progress.last().fraction)
        assertTrue("прогресс растёт", progress.zipWithNext().all { (a, b) -> b.doneBytes >= a.doneBytes })
        assertArrayEquals(content, store.locator.file(model).readBytes())
        assertTrue(store.missing(listOf(model)).isEmpty())
    }

    @Test fun resumesAfterDrop() = runBlocking {
        mode = "drop-once"
        val store = ModelStore(tmp.root)
        store.download(listOf(model)).toList()
        assertArrayEquals(content, store.locator.file(model).readBytes())
        assertEquals("два запроса: целиком и докачка", 2, ranges.size)
        assertTrue("второй запрос — докачка: $ranges", ranges.last()?.startsWith("bytes=") == true && ranges.last() != "bytes=0-")
    }

    @Test fun corruptFileIsNotAccepted() = runBlocking {
        mode = "corrupt"
        val store = ModelStore(tmp.root)
        try {
            store.download(listOf(model)).toList()
            fail("битый файл не должен приниматься")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("контрольная сумма"))
        }
        assertEquals("одна попытка и одна заново", 2, ranges.size)
        assertFalse(store.locator.file(model).exists())
    }

    @Test fun readyFilesAreNotDownloadedAgain() = runBlocking {
        val store = ModelStore(tmp.root)
        store.download(listOf(model)).toList()
        val requests = ranges.size
        store.download(listOf(model)).toList()
        assertEquals(requests, ranges.size)
    }
}
