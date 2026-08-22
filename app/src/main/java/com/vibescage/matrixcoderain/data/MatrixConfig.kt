package com.vibescage.matrixcoderain.data

/**
 * Estado y configuración de la simulación de Matrix Code Rain
 */
data class MatrixConfig(
    val numColumns: Int = 32,                 // 8 a 90 columnas
    val maxDropsPerCol: Int = 5,              // 1 a 6 gotas simultáneas por columna
    val rainIntensityPercent: Int = 55,       // 10 a 75% caudal de lluvia
    val trailNeonPercent: Int = 50,           // 0% a 100% en pasos de 10 (0%=1 letra, 100%=11 letras)
    val staticGridMode: Boolean = true,       // Caracteres fijos en cuadrícula con iluminación
    val palette: MatrixPalette = MatrixPalette.MATRIX,
    val speedMultiplier: Float = 0.5f,        // 0.1f a 3.0f
    val fadeOpacity: Float = 0.07f,           // 0.03f a 0.20f (longitud de estela)
    val enableGlow: Boolean = true,           // Resplandor neón bloom general
    val enableCRT: Boolean = true,            // Líneas CRT scanlines
    val enableTouch: Boolean = true,          // Interactividad táctil
    val enableAudio: Boolean = false,         // Sonido de lluvia
    val isPaused: Boolean = false
) {
    /**
     * Calcula cuántas letras deben recibir resplandor neón en el rastro:
     * 0% -> 1 letra (la cabeza)
     * 10% -> 2 letras
     * ...
     * 100% -> 11 letras
     */
    val numGlowChars: Int
        get() = 1 + (trailNeonPercent / 10).coerceIn(0, 10)
}
