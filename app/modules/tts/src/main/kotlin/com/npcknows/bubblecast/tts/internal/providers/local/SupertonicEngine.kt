package com.npcknows.bubblecast.tts.internal.providers.local

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.io.File
import java.nio.FloatBuffer
import java.nio.LongBuffer
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt

/** Голос Supertonic: два «отпечатка» стиля из файла voice_styles/<голос>.json. */
internal class VoiceStyle(val ttl: OnnxTensor, val dp: OnnxTensor)

/**
 * Supertonic-3: длительность → кодирование текста → очистка шума за [steps] шагов → вокодер.
 * Перенос TextToSpeech из примера Supertone (github.com/supertone-inc/supertonic, MIT, © 2025 Supertone Inc.).
 * Модели работают по одной реплике за раз (batch = 1) — реплики в комиксах короткие.
 */
internal class SupertonicEngine(
    durationPredictor: File,
    textEncoder: File,
    vectorEstimator: File,
    vocoder: File,
    config: File,
    indexer: File,
) : AutoCloseable {
    private val env = OrtEnvironment.getEnvironment()
    private val options = OrtSession.SessionOptions()
    private val dp = env.createSession(durationPredictor.path, options)
    private val enc = env.createSession(textEncoder.path, options)
    private val est = env.createSession(vectorEstimator.path, options)
    private val voc = env.createSession(vocoder.path, options)
    private val text: SupertonicText
    val sampleRate: Int
    private val chunkSize: Int
    private val latentDim: Int

    init {
        val cfg = Json.parseToJsonElement(config.readText()).jsonObject
        val ae = cfg.getValue("ae").jsonObject
        val ttl = cfg.getValue("ttl").jsonObject
        sampleRate = ae.getValue("sample_rate").jsonPrimitive.int
        val compress = ttl.getValue("chunk_compress_factor").jsonPrimitive.int
        chunkSize = ae.getValue("base_chunk_size").jsonPrimitive.int * compress
        latentDim = ttl.getValue("latent_dim").jsonPrimitive.int * compress
        val idx = Json.parseToJsonElement(indexer.readText()).jsonArray
        text = SupertonicText(LongArray(idx.size) { idx[it].jsonPrimitive.long })
    }

    fun loadStyle(file: File): VoiceStyle {
        val root = Json.parseToJsonElement(file.readText()).jsonObject
        return VoiceStyle(tensorOf(root.getValue("style_ttl").jsonObject), tensorOf(root.getValue("style_dp").jsonObject))
    }

    /** Звук одной реплики, отсчёты −1..1. [seed] делает результат повторяемым (один текст — один звук). */
    fun synthesize(input: String, lang: String, style: VoiceStyle, steps: Int, speed: Float, seed: Long): FloatArray {
        val ids = text.ids(input, lang)
        val n = ids.size.toLong()
        val use = mutableListOf<AutoCloseable>()
        try {
            val idsT = OnnxTensor.createTensor(env, LongBuffer.wrap(ids), longArrayOf(1, n)).also(use::add)
            val maskT = ones(n).also(use::add)
            val duration = dp.run(mapOf("text_ids" to idsT, "style_dp" to style.dp, "text_mask" to maskT)).also(use::add)
                .let { (it[0].value as FloatArray)[0] } / speed
            val emb = enc.run(mapOf("text_ids" to idsT, "style_ttl" to style.ttl, "text_mask" to maskT)).also(use::add)[0] as OnnxTensor
            val wavLen = (duration * sampleRate).toInt()
            val latentLen = ((wavLen + chunkSize - 1) / chunkSize).toLong()
            var latent = noise(latentLen.toInt(), seed)
            val latentMaskT = ones(latentLen).also(use::add)
            val totalT = OnnxTensor.createTensor(env, floatArrayOf(steps.toFloat())).also(use::add)
            val shape = longArrayOf(1, latentDim.toLong(), latentLen)
            for (step in 0 until steps) {
                OnnxTensor.createTensor(env, FloatBuffer.wrap(latent), shape).use { xt ->
                    OnnxTensor.createTensor(env, floatArrayOf(step.toFloat())).use { stepT ->
                        est.run(
                            mapOf(
                                "noisy_latent" to xt, "text_emb" to emb, "style_ttl" to style.ttl,
                                "latent_mask" to latentMaskT, "text_mask" to maskT,
                                "current_step" to stepT, "total_step" to totalT,
                            ),
                        ).use { r -> latent = (r[0] as OnnxTensor).floatBuffer.let { b -> FloatArray(b.remaining()).also(b::get) } }
                    }
                }
            }
            val wav = OnnxTensor.createTensor(env, FloatBuffer.wrap(latent), shape).use { xt ->
                voc.run(mapOf("latent" to xt)).use { r -> (r[0] as OnnxTensor).floatBuffer.let { b -> FloatArray(b.remaining()).also(b::get) } }
            }
            return wav.copyOf(minOf(wavLen, wav.size))
        } finally {
            use.asReversed().forEach(AutoCloseable::close)
        }
    }

    override fun close() {
        listOf(dp, enc, est, voc).forEach(OrtSession::close)
        options.close()
    }

    private fun ones(len: Long) = OnnxTensor.createTensor(env, FloatBuffer.wrap(FloatArray(len.toInt()) { 1f }), longArrayOf(1, 1, len))

    /** Нормальный шум (Бокса — Мюллера), как в примере Supertone. */
    private fun noise(latentLen: Int, seed: Long): FloatArray {
        val rng = Random(seed)
        return FloatArray(latentDim * latentLen) {
            val u1 = maxOf(1e-10, rng.nextDouble())
            (sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * rng.nextDouble())).toFloat()
        }
    }

    private fun tensorOf(node: JsonObject): OnnxTensor {
        val dims = node.getValue("dims").jsonArray.map { it.jsonPrimitive.long }.toLongArray()
        val flat = FloatArray(dims.fold(1L, Long::times).toInt())
        var i = 0
        fun walk(e: kotlinx.serialization.json.JsonElement) {
            if (e is JsonArray) e.forEach(::walk) else flat[i++] = e.jsonPrimitive.float
        }
        walk(node.getValue("data"))
        return OnnxTensor.createTensor(env, FloatBuffer.wrap(flat), dims)
    }
}
