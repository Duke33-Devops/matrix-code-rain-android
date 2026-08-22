package com.vibescage.matrixcoderain.engine

import kotlin.random.Random

/**
 * Representa una gota individual de lluvia digital que desciende por una columna
 */
class MatrixDrop(
    var headRow: Float,
    var speed: Float,
    val length: Int,
    val chars: CharArray,
    var active: Boolean,
    var respawnDelay: Float,
    var glow: Boolean = true,
    var leadFlickerTimer: Float = Random.nextFloat() * 2f
) {
    companion object {
        fun create(
            totalRows: Int,
            isActive: Boolean = true,
            initial: Boolean = false,
            dropIndex: Int = 0,
            charSupplier: () -> Char
        ): MatrixDrop {
            val length = Random.nextInt(8, 26)
            val speed = Random.nextFloat() * 8f + 4f // 4 a 12 filas/segundo base

            val headRow = if (initial && isActive) {
                Random.nextFloat() * totalRows
            } else {
                -length.toFloat() - Random.nextFloat() * 15f - (dropIndex * 10f)
            }

            val chars = CharArray(length + 5) { charSupplier() }

            return MatrixDrop(
                headRow = headRow,
                speed = speed,
                length = length,
                chars = chars,
                active = isActive,
                respawnDelay = if (isActive) 0f else Random.nextFloat() * 3.5f + 0.5f,
                glow = Random.nextFloat() > 0.15f
            )
        }
    }
}
