package com.vibescage.matrixcoderain.service

import android.service.dreams.DreamService
import android.view.ViewGroup
import com.vibescage.matrixcoderain.data.MatrixPreferences
import com.vibescage.matrixcoderain.ui.MatrixCanvasView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Salvapantallas (DreamService / Daydream) de Matrix Code Rain para Android.
 * Se activa cuando el dispositivo está en la base de carga o pantalla de reposo.
 */
class MatrixDreamService : DreamService() {

    private var canvasView: MatrixCanvasView? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true

        val prefs = MatrixPreferences(applicationContext)
        val view = MatrixCanvasView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        canvasView = view
        setContentView(view)

        scope.launch {
            prefs.configFlow.collectLatest { config ->
                view.engine.config = config
            }
        }
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        canvasView?.startAnimation()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        canvasView?.stopAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
        canvasView?.stopAnimation()
        canvasView = null
    }
}
