package com.vibescage.matrixcoderain.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "matrix_prefs")

class MatrixPreferences(private val context: Context) {

    private object Keys {
        val NUM_COLUMNS = intPreferencesKey("num_columns")
        val MAX_DROPS_PER_COL = intPreferencesKey("max_drops_per_col")
        val RAIN_INTENSITY = intPreferencesKey("rain_intensity")
        val TRAIL_NEON = intPreferencesKey("trail_neon")
        val STATIC_GRID_MODE = booleanPreferencesKey("static_grid_mode")
        val PALETTE = stringPreferencesKey("palette")
        val SPEED_MULTIPLIER = floatPreferencesKey("speed_multiplier")
        val FADE_OPACITY = floatPreferencesKey("fade_opacity")
        val ENABLE_GLOW = booleanPreferencesKey("enable_glow")
        val ENABLE_CRT = booleanPreferencesKey("enable_crt")
        val ENABLE_TOUCH = booleanPreferencesKey("enable_touch")
        val ENABLE_AUDIO = booleanPreferencesKey("enable_audio")
    }

    val configFlow: Flow<MatrixConfig> = context.dataStore.data.map { prefs ->
        MatrixConfig(
            numColumns = prefs[Keys.NUM_COLUMNS] ?: 32,
            maxDropsPerCol = prefs[Keys.MAX_DROPS_PER_COL] ?: 5,
            rainIntensityPercent = prefs[Keys.RAIN_INTENSITY] ?: 55,
            trailNeonPercent = prefs[Keys.TRAIL_NEON] ?: 50,
            staticGridMode = prefs[Keys.STATIC_GRID_MODE] ?: true,
            palette = MatrixPalette.fromId(prefs[Keys.PALETTE] ?: "matrix"),
            speedMultiplier = prefs[Keys.SPEED_MULTIPLIER] ?: 0.5f,
            fadeOpacity = prefs[Keys.FADE_OPACITY] ?: 0.07f,
            enableGlow = prefs[Keys.ENABLE_GLOW] ?: true,
            enableCRT = prefs[Keys.ENABLE_CRT] ?: true,
            enableTouch = prefs[Keys.ENABLE_TOUCH] ?: true,
            enableAudio = prefs[Keys.ENABLE_AUDIO] ?: false
        )
    }

    suspend fun saveConfig(config: MatrixConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NUM_COLUMNS] = config.numColumns
            prefs[Keys.MAX_DROPS_PER_COL] = config.maxDropsPerCol
            prefs[Keys.RAIN_INTENSITY] = config.rainIntensityPercent
            prefs[Keys.TRAIL_NEON] = config.trailNeonPercent
            prefs[Keys.STATIC_GRID_MODE] = config.staticGridMode
            prefs[Keys.PALETTE] = config.palette.id
            prefs[Keys.SPEED_MULTIPLIER] = config.speedMultiplier
            prefs[Keys.FADE_OPACITY] = config.fadeOpacity
            prefs[Keys.ENABLE_GLOW] = config.enableGlow
            prefs[Keys.ENABLE_CRT] = config.enableCRT
            prefs[Keys.ENABLE_TOUCH] = config.enableTouch
            prefs[Keys.ENABLE_AUDIO] = config.enableAudio
        }
    }
}
