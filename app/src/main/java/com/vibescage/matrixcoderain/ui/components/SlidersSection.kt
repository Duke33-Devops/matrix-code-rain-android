package com.vibescage.matrixcoderain.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibescage.matrixcoderain.R
import com.vibescage.matrixcoderain.data.MatrixConfig
import kotlin.math.roundToInt

@Composable
fun SlidersSection(
    config: MatrixConfig,
    onConfigChange: (MatrixConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = Color(config.palette.primaryColor)
    val sliderColors = SliderDefaults.colors(
        thumbColor = themeColor,
        activeTrackColor = themeColor,
        inactiveTrackColor = Color(0x33FFFFFF)
    )
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = themeColor,
        checkedTrackColor = themeColor.copy(alpha = 0.35f),
        uncheckedThumbColor = Color(0x88FFFFFF),
        uncheckedTrackColor = Color(0x22FFFFFF)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Título Sección
        Text(
            text = stringResource(R.string.sec_columns_title),
            style = MaterialTheme.typography.titleMedium,
            color = themeColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 1. Columnas (8 a 90)
        SliderRow(
            label = stringResource(R.string.label_columns),
            valueText = "${config.numColumns} cols",
            themeColor = themeColor
        ) {
            Slider(
                value = config.numColumns.toFloat(),
                onValueChange = { onConfigChange(config.copy(numColumns = it.roundToInt())) },
                valueRange = 8f..90f,
                steps = 81,
                colors = sliderColors
            )
        }

        // 2. Máx Gotas por Columna (1 a 6)
        SliderRow(
            label = stringResource(R.string.label_max_drops),
            valueText = "${config.maxDropsPerCol} gotas",
            themeColor = themeColor
        ) {
            Slider(
                value = config.maxDropsPerCol.toFloat(),
                onValueChange = { onConfigChange(config.copy(maxDropsPerCol = it.roundToInt())) },
                valueRange = 1f..6f,
                steps = 4,
                colors = sliderColors
            )
        }

        // 3. Caudal / Intensidad de Lluvia (10 a 75%)
        SliderRow(
            label = stringResource(R.string.label_rain_density),
            valueText = "${config.rainIntensityPercent}% (Máx 75%)",
            hint = stringResource(R.string.hint_rain_density),
            themeColor = themeColor
        ) {
            Slider(
                value = config.rainIntensityPercent.toFloat(),
                onValueChange = { onConfigChange(config.copy(rainIntensityPercent = it.roundToInt())) },
                valueRange = 10f..75f,
                steps = 12,
                colors = sliderColors
            )
        }

        // 4. Modo Cuadrícula Estática Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_static_grid),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.hint_static_grid),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0x99FFFFFF),
                    fontSize = 10.sp
                )
            }
            Switch(
                checked = config.staticGridMode,
                onCheckedChange = { onConfigChange(config.copy(staticGridMode = it)) },
                colors = switchColors
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Título Sección: Velocidad y Estela
        Text(
            text = stringResource(R.string.sec_motion_title),
            style = MaterialTheme.typography.titleMedium,
            color = themeColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 5. Alcance Neón en Rastro (0% a 100% en pasos de 10) -> 1 a 11 letras
        val glowLetters = 1 + (config.trailNeonPercent / 10)
        SliderRow(
            label = stringResource(R.string.label_trail_neon),
            valueText = "${config.trailNeonPercent}% ($glowLetters letras)",
            hint = stringResource(R.string.hint_trail_neon),
            themeColor = themeColor
        ) {
            Slider(
                value = config.trailNeonPercent.toFloat(),
                onValueChange = { onConfigChange(config.copy(trailNeonPercent = (it / 10f).roundToInt() * 10)) },
                valueRange = 0f..100f,
                steps = 9,
                colors = sliderColors
            )
        }

        // 6. Velocidad de Avance
        SliderRow(
            label = stringResource(R.string.label_speed),
            valueText = String.format("%.2fx", config.speedMultiplier),
            themeColor = themeColor
        ) {
            Slider(
                value = config.speedMultiplier,
                onValueChange = { onConfigChange(config.copy(speedMultiplier = (it * 100).roundToInt() / 100f)) },
                valueRange = 0.05f..2.5f,
                colors = sliderColors
            )
        }

        // 7. Longitud de Estela / Fade Opacity
        SliderRow(
            label = stringResource(R.string.label_fade),
            valueText = String.format("%.2f", config.fadeOpacity),
            themeColor = themeColor
        ) {
            Slider(
                value = config.fadeOpacity,
                onValueChange = { onConfigChange(config.copy(fadeOpacity = (it * 100).roundToInt() / 100f)) },
                valueRange = 0.03f..0.20f,
                colors = sliderColors
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Switches de Efectos
        ToggleRow(
            label = stringResource(R.string.label_glow),
            checked = config.enableGlow,
            onCheckedChange = { onConfigChange(config.copy(enableGlow = it)) },
            switchColors = switchColors
        )
        ToggleRow(
            label = stringResource(R.string.label_crt),
            checked = config.enableCRT,
            onCheckedChange = { onConfigChange(config.copy(enableCRT = it)) },
            switchColors = switchColors
        )
        ToggleRow(
            label = stringResource(R.string.label_touch),
            checked = config.enableTouch,
            onCheckedChange = { onConfigChange(config.copy(enableTouch = it)) },
            switchColors = switchColors
        )
        ToggleRow(
            label = stringResource(R.string.label_audio),
            hint = stringResource(R.string.hint_audio),
            checked = config.enableAudio,
            onCheckedChange = { onConfigChange(config.copy(enableAudio = it)) },
            switchColors = switchColors
        )
    }
}

@Composable
private fun SliderRow(
    label: String,
    valueText: String,
    hint: String? = null,
    themeColor: Color,
    sliderContent: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall,
                color = themeColor
            )
        }
        if (hint != null) {
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x88FFFFFF),
                fontSize = 10.sp
            )
        }
        sliderContent()
    }
}

@Composable
private fun ToggleRow(
    label: String,
    hint: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchColors: SwitchColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0x88FFFFFF),
                    fontSize = 10.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors
        )
    }
}
