package com.vibescage.matrixcoderain.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Typeface
import com.vibescage.matrixcoderain.data.MatrixConfig
import com.vibescage.matrixcoderain.data.MatrixPalette
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Motor central de renderizado 2D/Canvas ultra-optimizado de Matrix Code Rain.
 * Compatible tanto con Compose/Views interactivas como con WallpaperService en SurfaceHolder.
 */
class MatrixEngine {

    // Glifos auténticos de Matrix: Katakana medio ancho, números, símbolos y binario
    private val charSet = (
        "ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾂﾃﾅﾆﾇﾈﾊﾋﾎﾏﾐﾑﾒﾓﾔﾕﾗﾘﾜﾝ" +
        "0123456789" +
        "ABCDEFGHJKLMNPQRSTUVWXYZ" +
        "abcdefghijklmnopqrstuvwxyz" +
        "+-*/=%<>:~!?@#$&[]{}|░▒▓"
    ).toCharArray()

    // Configuración actual
    var config: MatrixConfig = MatrixConfig()
        set(value) {
            val oldCols = field.numColumns
            val oldMaxDrops = field.maxDropsPerCol
            field = value
            if (width > 0 && height > 0 && (oldCols != value.numColumns || oldMaxDrops != value.maxDropsPerCol)) {
                initStructure()
            }
        }

    // Dimensiones
    var width: Int = 0
        private set
    var height: Int = 0
        private set

    private var cellWidth: Float = 0f
    private var cellHeight: Float = 0f
    private var totalRows: Int = 0

    // Estructura de la Matriz
    private val grid = mutableListOf<MutableList<MatrixCell>>() // grid[col][row]
    private val columns = mutableListOf<MatrixColumn>()

    // Paints preasignados para cero asignación de memoria por frame (evitar GC churn)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
    }

    private val fadePaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val crtPaint = Paint().apply {
        color = Color.argb(35, 0, 0, 0)
        strokeWidth = 2f
    }

    private val shockwavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val terminalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        isFakeBoldText = true
    }

    // Bitmap persistente para estelas suaves
    private var trailBitmap: Bitmap? = null
    private var trailCanvas: Canvas? = null

    // Touch / Shockwave
    private var shockwaveX = -1000f
    private var shockwaveY = -1000f
    private var shockwaveTime = 0f
    private val shockwaveMaxTime = 0.8f

    // Inyección de mensaje tipo terminal
    private var terminalText: String? = null
    private var terminalVisibleChars: Int = 0
    private var terminalTimer: Float = 0f
    private var terminalCursorBlink: Float = 0f

    fun setSize(w: Int, h: Int) {
        if (w <= 0 || h <= 0) return
        if (w != width || h != height || trailBitmap == null) {
            width = w
            height = h

            // Crear bitmap de estela
            trailBitmap?.recycle()
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            trailBitmap = bmp
            trailCanvas = Canvas(bmp).apply {
                drawColor(Color.BLACK)
            }

            initStructure()
        }
    }

    fun initStructure() {
        if (width <= 0 || height <= 0) return

        val numCols = config.numColumns.coerceIn(8, 120)
        cellWidth = width.toFloat() / numCols
        cellHeight = max(14f, (cellWidth * 1.35f).roundToInt().toFloat())
        totalRows = ceil(height / cellHeight).toInt() + 2

        grid.clear()
        columns.clear()

        val maxDrops = config.maxDropsPerCol.coerceIn(1, 6)
        val rainFactor = (config.rainIntensityPercent / 100f).coerceAtMost(0.75f) / 0.75f

        for (c in 0 until numCols) {
            // 1. Celdas de cuadrícula fija
            val colGrid = mutableListOf<MatrixCell>()
            for (r in 0 until totalRows) {
                colGrid.add(MatrixCell(getRandomChar(), Random.nextFloat() * 28f + 2f))
            }
            grid.add(colGrid)

            // 2. Gotas simultáneas por columna
            val col = MatrixColumn(c, (c + 0.5f) * cellWidth)
            for (d in 0 until maxDrops) {
                val slotProb = if (d == 0) max(0.6f, rainFactor) else rainFactor * (1f - (d.toFloat() / maxDrops) * 0.35f)
                val isActive = Random.nextFloat() < slotProb
                col.drops.add(MatrixDrop.create(totalRows, isActive, initial = true, dropIndex = d) { getRandomChar() })
            }
            columns.add(col)
        }
    }

    private fun getRandomChar(): Char = charSet[Random.nextInt(charSet.size)]

    /**
     * Dispara una onda de choque táctil en las coordenadas especificadas
     */
    fun triggerTouch(x: Float, y: Float) {
        if (!config.enableTouch) return
        shockwaveX = x
        shockwaveY = y
        shockwaveTime = shockwaveMaxTime

        // Mutar caracteres cercanos al toque
        val colIndex = (x / cellWidth).toInt().coerceIn(0, grid.size - 1)
        val rowIndex = (y / cellHeight).toInt().coerceIn(0, totalRows - 1)

        val radius = 3
        for (c in max(0, colIndex - radius)..min(grid.size - 1, colIndex + radius)) {
            for (r in max(0, rowIndex - radius)..min(totalRows - 1, rowIndex + radius)) {
                grid[c][r].char = getRandomChar()
            }
        }
    }

    /**
     * Inyecta un mensaje en la terminal con efecto máquina de escribir
     */
    fun injectMessage(message: String) {
        terminalText = message
        terminalVisibleChars = 0
        terminalTimer = 0f
    }

    /**
     * Renderiza un frame completo en el Canvas de destino
     */
    fun render(targetCanvas: Canvas, deltaTime: Float) {
        if (width <= 0 || height <= 0 || trailCanvas == null || trailBitmap == null) return

        val dt = deltaTime.coerceIn(0.001f, 0.1f)
        val palette = config.palette
        val tCanvas = trailCanvas ?: return

        // 1. Limpieza suave sobre el bitmap persistente para mantener la estela
        val alpha = (config.fadeOpacity * 255).roundToInt().coerceIn(4, 255)
        fadePaint.color = Color.argb(alpha, 0, 0, 0)
        tCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fadePaint)

        // 2. Renderizar lluvia
        if (!config.isPaused) {
            if (config.staticGridMode) {
                renderStaticGrid(tCanvas, palette, dt)
            } else {
                renderSlidingMode(tCanvas, palette, dt)
            }
        }

        // 3. Renderizar onda de choque si está activa
        if (shockwaveTime > 0f) {
            renderShockwave(tCanvas, palette, dt)
        }

        // 4. Copiar bitmap persistente al Canvas de destino
        targetCanvas.drawBitmap(trailBitmap!!, 0f, 0f, null)

        // 5. Efecto CRT scanlines sobre el targetCanvas
        if (config.enableCRT) {
            renderCRT(targetCanvas)
        }

        // 6. Mensaje Terminal Decodificado estilo "Wake up, Neo..."
        if (terminalText != null) {
            renderTerminalOverlay(targetCanvas, palette, dt)
        }
    }

    private fun renderStaticGrid(canvas: Canvas, palette: MatrixPalette, dt: Float) {
        val fontSize = (cellWidth * 0.9f)
        textPaint.textSize = fontSize
        glowPaint.textSize = fontSize

        val speedMult = config.speedMultiplier
        val numGlowChars = config.numGlowChars
        val rainFactor = (config.rainIntensityPercent / 100f).coerceAtMost(0.75f) / 0.75f
        val maxDrops = config.maxDropsPerCol

        for (c in 0 until columns.size) {
            val col = columns[c]
            val colX = col.x
            val gridCol = grid[c]

            // 1. Actualizar gotas
            for (d in 0 until col.drops.size) {
                val drop = col.drops[d]

                if (!drop.active) {
                    drop.respawnDelay -= dt
                    if (drop.respawnDelay <= 0f) {
                        val respawnProb = if (d == 0) max(0.5f, rainFactor) else rainFactor * 0.8f
                        if (Random.nextFloat() < respawnProb) {
                            drop.active = true
                            drop.headRow = -drop.length - Random.nextFloat() * 10f
                            drop.speed = Random.nextFloat() * 8f + 4f
                            drop.glow = Random.nextFloat() > 0.15f
                        } else {
                            drop.respawnDelay = Random.nextFloat() * 3f + 1f
                        }
                    }
                    continue
                }

                drop.headRow += drop.speed * speedMult * dt * 10f

                // Dibujar cabeza y rastro iluminado en la cuadrícula fija
                val currentHead = drop.headRow.toInt()
                val tailEnd = currentHead - drop.length

                // Si la gota ya cruzó toda la pantalla
                if (tailEnd > totalRows) {
                    drop.active = false
                    drop.respawnDelay = Random.nextFloat() * 3f + 0.5f
                    continue
                }

                // Renderizar caracteres de la gota
                for (step in 0 until drop.length) {
                    val row = currentHead - step
                    if (row !in 0 until totalRows) continue

                    val cell = gridCol[row]
                    val y = (row + 0.5f) * cellHeight

                    // Mutación sutil de caracteres
                    cell.mutationTimer -= dt
                    if (cell.mutationTimer <= 0f) {
                        cell.char = getRandomChar()
                        cell.mutationTimer = Random.nextFloat() * 25f + 3f
                    }

                    if (step == 0) {
                        // CABEZA PRINCIPAL: Blanco resplandeciente
                        if (config.enableGlow && drop.glow) {
                            glowPaint.color = palette.leadColor
                            glowPaint.setShadowLayer(16f, 0f, 0f, palette.glowColor)
                            canvas.drawText(cell.char.toString(), colX, y, glowPaint)
                        } else {
                            textPaint.color = palette.leadColor
                            textPaint.clearShadowLayer()
                            canvas.drawText(cell.char.toString(), colX, y, textPaint)
                        }
                    } else if (step < numGlowChars) {
                        // RASTRO NEÓN (0% a 100% en pasos de 10% -> 1 a 11 letras con degradado)
                        val glowRatio = 1f - (step.toFloat() / numGlowChars)
                        val shadowRadius = 12f * glowRatio
                        val charColor = if (step == 1) palette.primaryColor else palette.midColor

                        if (config.enableGlow && shadowRadius > 1f) {
                            glowPaint.color = charColor
                            glowPaint.setShadowLayer(shadowRadius, 0f, 0f, palette.glowColor)
                            canvas.drawText(cell.char.toString(), colX, y, glowPaint)
                        } else {
                            textPaint.color = charColor
                            textPaint.clearShadowLayer()
                            canvas.drawText(cell.char.toString(), colX, y, textPaint)
                        }
                    } else {
                        // COLA ATENUADA
                        val fadeRatio = (drop.length - step).toFloat() / (drop.length - numGlowChars).coerceAtLeast(1)
                        if (fadeRatio > 0.5f) {
                            textPaint.color = palette.midColor
                        } else {
                            textPaint.color = palette.dimColor
                        }
                        textPaint.clearShadowLayer()
                        canvas.drawText(cell.char.toString(), colX, y, textPaint)
                    }
                }
            }
        }
    }

    private fun renderSlidingMode(canvas: Canvas, palette: MatrixPalette, dt: Float) {
        val fontSize = (cellWidth * 0.9f)
        textPaint.textSize = fontSize
        glowPaint.textSize = fontSize

        val speedMult = config.speedMultiplier
        val numGlowChars = config.numGlowChars

        for (col in columns) {
            val colX = col.x
            for (drop in col.drops) {
                if (!drop.active) continue

                drop.headRow += drop.speed * speedMult * dt * 10f
                val head = drop.headRow.toInt()

                if (head - drop.length > totalRows) {
                    drop.headRow = -drop.length - Random.nextFloat() * 12f
                    drop.speed = Random.nextFloat() * 8f + 4f
                }

                for (step in 0 until drop.length) {
                    val row = head - step
                    if (row !in 0 until totalRows) continue

                    val y = (row + 0.5f) * cellHeight
                    val char = drop.chars[step % drop.chars.size]

                    if (step == 0) {
                        if (config.enableGlow) {
                            glowPaint.color = palette.leadColor
                            glowPaint.setShadowLayer(16f, 0f, 0f, palette.glowColor)
                            canvas.drawText(char.toString(), colX, y, glowPaint)
                        } else {
                            textPaint.color = palette.leadColor
                            textPaint.clearShadowLayer()
                            canvas.drawText(char.toString(), colX, y, textPaint)
                        }
                    } else if (step < numGlowChars) {
                        val glowRatio = 1f - (step.toFloat() / numGlowChars)
                        glowPaint.color = palette.primaryColor
                        glowPaint.setShadowLayer(10f * glowRatio, 0f, 0f, palette.glowColor)
                        canvas.drawText(char.toString(), colX, y, glowPaint)
                    } else {
                        textPaint.color = if (step < drop.length / 2) palette.midColor else palette.dimColor
                        textPaint.clearShadowLayer()
                        canvas.drawText(char.toString(), colX, y, textPaint)
                    }
                }
            }
        }
    }

    private fun renderShockwave(canvas: Canvas, palette: MatrixPalette, dt: Float) {
        shockwaveTime -= dt
        val progress = 1f - (shockwaveTime / shockwaveMaxTime).coerceIn(0f, 1f)
        val maxRadius = max(width, height) * 0.45f
        val currentRadius = progress * maxRadius
        val alpha = ((1f - progress) * 200).toInt().coerceIn(0, 255)

        shockwavePaint.color = palette.primaryColor
        shockwavePaint.alpha = alpha
        shockwavePaint.strokeWidth = (6f * (1f - progress)).coerceAtLeast(1.5f)

        canvas.drawCircle(shockwaveX, shockwaveY, currentRadius, shockwavePaint)
    }

    private fun renderCRT(canvas: Canvas) {
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, crtPaint)
            y += 4f
        }
    }

    private fun renderTerminalOverlay(canvas: Canvas, palette: MatrixPalette, dt: Float) {
        val text = terminalText ?: return

        terminalTimer += dt
        if (terminalTimer >= 0.05f && terminalVisibleChars < text.length) {
            terminalVisibleChars++
            terminalTimer = 0f
        }

        terminalCursorBlink = (terminalCursorBlink + dt * 4f) % 2f
        val showCursor = terminalCursorBlink < 1f

        val displayString = text.substring(0, terminalVisibleChars) + if (showCursor) "_" else " "

        terminalPaint.textSize = (cellWidth * 1.15f).coerceAtLeast(22f)
        terminalPaint.color = palette.leadColor
        terminalPaint.setShadowLayer(12f, 0f, 0f, palette.glowColor)
        terminalPaint.textAlign = Paint.Align.CENTER

        val centerX = width / 2f
        val centerY = height / 2f

        // Fondo oscuro semitransparente para legibilidad
        val bgPaint = Paint().apply {
            color = Color.argb(190, 4, 6, 8)
        }
        val bounds = Rect()
        terminalPaint.getTextBounds(displayString, 0, displayString.length, bounds)
        val pad = 24f
        canvas.drawRect(
            centerX - bounds.width() / 2f - pad,
            centerY - bounds.height() - pad,
            centerX + bounds.width() / 2f + pad,
            centerY + pad,
            bgPaint
        )

        canvas.drawText(displayString, centerX, centerY, terminalPaint)
    }
}
