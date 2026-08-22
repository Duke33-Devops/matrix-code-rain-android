package com.vibescage.matrixcoderain.service

import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.vibescage.matrixcoderain.data.MatrixPreferences
import com.vibescage.matrixcoderain.engine.MatrixEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Servicio de Fondo de Pantalla Animado (Live Wallpaper) de Matrix Code Rain.
 * Altamente optimizado para ahorrar batería cuando la pantalla está apagada o cubierta.
 */
class MatrixWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MatrixWallpaperEngine()
    }

    private inner class MatrixWallpaperEngine : Engine() {

        private val matrixEngine = MatrixEngine()
        private val prefs = MatrixPreferences(applicationContext)
        private val scope = CoroutineScope(Dispatchers.Main + Job())
        private val handler = Handler(Looper.getMainLooper())

        private var isEngineVisible = false
        private var lastFrameTime = System.nanoTime()

        private val drawRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                if (isEngineVisible) {
                    handler.postDelayed(this, 16) // ~60 FPS
                }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)

            // Observar cambios de preferencias en tiempo real
            scope.launch {
                prefs.configFlow.collectLatest { newConfig ->
                    matrixEngine.config = newConfig
                }
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            matrixEngine.setSize(width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isEngineVisible = visible
            if (visible) {
                lastFrameTime = System.nanoTime()
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                matrixEngine.triggerTouch(event.x, event.y)
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder ?: return
            var canvas: android.graphics.Canvas? = null

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val now = System.nanoTime()
                    val dt = ((now - lastFrameTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    lastFrameTime = now

                    matrixEngine.render(canvas, dt)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            isEngineVisible = false
            handler.removeCallbacks(drawRunnable)
            scope.cancel()
        }
    }
}
