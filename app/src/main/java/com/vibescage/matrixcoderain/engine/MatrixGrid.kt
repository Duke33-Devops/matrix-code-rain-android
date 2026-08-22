package com.vibescage.matrixcoderain.engine

import kotlin.random.Random

/**
 * Celda fija en la cuadrícula 2D para el modo de cuadrícula estática
 */
data class MatrixCell(
    var char: Char,
    var mutationTimer: Float = Random.nextFloat() * 28f + 2f
)

/**
 * Columna que agrupa las gotas y sus celdas
 */
class MatrixColumn(
    val colIndex: Int,
    var x: Float,
    val drops: MutableList<MatrixDrop> = mutableListOf()
)
