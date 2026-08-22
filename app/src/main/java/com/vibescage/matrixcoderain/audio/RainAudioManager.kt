package com.vibescage.matrixcoderain.audio

import android.content.Context
import android.media.MediaPlayer
import com.vibescage.matrixcoderain.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Gestor de audio para la reproducción continua y fundidos de sonido de lluvia ambiental
 */
class RainAudioManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var fadeJob: Job? = null
    private var isPlaying = false
    private var targetVolume = 0.8f

    fun start() {
        if (isPlaying) return
        isPlaying = true

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.lluvia).apply {
                    isLooping = true
                    setVolume(0f, 0f)
                }
            }
            mediaPlayer?.start()
            fadeVolume(from = 0f, to = targetVolume, durationMs = 1200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        if (!isPlaying) return
        isPlaying = false

        fadeVolume(from = targetVolume, to = 0f, durationMs = 800) {
            try {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fadeVolume(from: Float, to: Float, durationMs: Long, onComplete: (() -> Unit)? = null) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val steps = 20
            val interval = durationMs / steps
            val delta = (to - from) / steps

            var current = from
            for (i in 0 until steps) {
                current += delta
                val vol = current.coerceIn(0f, 1f)
                try {
                    mediaPlayer?.setVolume(vol, vol)
                } catch (e: Exception) {
                    break
                }
                delay(interval)
            }

            try {
                mediaPlayer?.setVolume(to, to)
            } catch (e: Exception) {
                // Ignore
            }
            onComplete?.invoke()
        }
    }

    fun release() {
        isPlaying = false
        fadeJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
