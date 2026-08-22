package com.vibescage.matrixcoderain.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import com.vibescage.matrixcoderain.engine.MatrixEngine

/**
 * Vista personalizada optimizada para renderizar MatrixEngine a la tasa de refresco nativa (60-120Hz)
 */
class MatrixCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    val engine = MatrixEngine()
    private var isAnimating = false
    private var lastFrameTimeNanos: Long = 0

    init {
        // Habilitar dibujo continuo
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        engine.setSize(w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            engine.triggerTouch(event.x, event.y)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val now = System.nanoTime()
        val dt = if (lastFrameTimeNanos > 0) {
            ((now - lastFrameTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
        } else {
            0.016f
        }
        lastFrameTimeNanos = now

        engine.render(canvas, dt)
    }

    fun startAnimation() {
        if (!isAnimating) {
            isAnimating = true
            lastFrameTimeNanos = 0
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun stopAnimation() {
        if (isAnimating) {
            isAnimating = false
            Choreographer.getInstance().removeFrameCallback(this)
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (isAnimating) {
            invalidate()
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}
