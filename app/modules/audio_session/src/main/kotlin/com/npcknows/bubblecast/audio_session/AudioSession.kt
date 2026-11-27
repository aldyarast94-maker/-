package com.npcknows.bubblecast.audio_session

import com.npcknows.bubblecast.shared.AudioClip
import kotlinx.coroutines.flow.Flow

enum class MediaCommand { PLAY, PAUSE, NEXT, PREVIOUS, STOP }

/** Очередь звуков → игра в фоне, экран блокировки, кнопки наушников и руля. */
interface AudioSession {
    /** Команды с кнопок наушников, руля, экрана блокировки. */
    val commands: Flow<MediaCommand>
    /** Играет клип и возвращается, когда он доиграл. */
    suspend fun play(clip: AudioClip)
    fun pause()
    fun resume()
    fun stop()
}
