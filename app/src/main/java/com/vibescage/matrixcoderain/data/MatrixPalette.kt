package com.vibescage.matrixcoderain.data

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Paletas de Color Cyberpunk auténticas para Matrix Code Rain
 */
enum class MatrixPalette(
    val id: String,
    val displayNameRes: Int,
    @ColorInt val leadColor: Int,
    @ColorInt val primaryColor: Int,
    @ColorInt val midColor: Int,
    @ColorInt val dimColor: Int,
    @ColorInt val glowColor: Int
) {
    MATRIX(
        id = "matrix",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_matrix,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#00FF66"),
        midColor = Color.parseColor("#00CC44"),
        dimColor = Color.parseColor("#005522"),
        glowColor = Color.parseColor("#00FF66")
    ),
    CYAN(
        id = "cyan",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_cyan,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#00F0FF"),
        midColor = Color.parseColor("#00B4D8"),
        dimColor = Color.parseColor("#004E64"),
        glowColor = Color.parseColor("#00F0FF")
    ),
    AMBER(
        id = "amber",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_amber,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#FFB000"),
        midColor = Color.parseColor("#D48800"),
        dimColor = Color.parseColor("#663C00"),
        glowColor = Color.parseColor("#FFB000")
    ),
    MAGENTA(
        id = "magenta",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_magenta,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#FF007F"),
        midColor = Color.parseColor("#C70062"),
        dimColor = Color.parseColor("#5E0030"),
        glowColor = Color.parseColor("#FF007F")
    ),
    RED(
        id = "red",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_red,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#FF2A2A"),
        midColor = Color.parseColor("#CC1111"),
        dimColor = Color.parseColor("#590000"),
        glowColor = Color.parseColor("#FF2A2A")
    ),
    WHITE(
        id = "white",
        displayNameRes = com.vibescage.matrixcoderain.R.string.palette_white,
        leadColor = Color.parseColor("#FFFFFF"),
        primaryColor = Color.parseColor("#E6F1FF"),
        midColor = Color.parseColor("#94A3B8"),
        dimColor = Color.parseColor("#334155"),
        glowColor = Color.parseColor("#E6F1FF")
    );

    companion object {
        fun fromId(id: String): MatrixPalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MATRIX
        }
    }
}
