package com.vibescage.matrixcoderain.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibescage.matrixcoderain.R
import com.vibescage.matrixcoderain.data.MatrixConfig
import com.vibescage.matrixcoderain.data.MatrixPalette

@Composable
fun CyberHudOverlay(
    config: MatrixConfig,
    isHudVisible: Boolean,
    onToggleHud: () -> Unit,
    onConfigChange: (MatrixConfig) -> Unit,
    onSetWallpaperClick: () -> Unit,
    onResetConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = Color(config.palette.primaryColor)

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Botón flotante para abrir HUD cuando está minimizado
        AnimatedVisibility(
            visible = !isHudVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xD0080B10))
                    .border(1.5.dp, themeColor, CircleShape)
                    .clickable { onToggleHud() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = themeColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // 2. Panel HUD Flotante (Glassmorphism Cyberpunk)
        AnimatedVisibility(
            visible = isHudVisible,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xE8080C14))
                    .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                // Header del HUD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(themeColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.hud_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.version_tag),
                        style = MaterialTheme.typography.labelSmall,
                        color = themeColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(
                        onClick = onToggleHud,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar HUD",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón Destacado: Establecer como Fondo de Pantalla Animado
                Button(
                    onClick = onSetWallpaperClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_set_wallpaper),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contenido desplazable
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Selector de Paleta
                    PaletteSelector(
                        selectedPalette = config.palette,
                        onSelectPalette = { onConfigChange(config.copy(palette = it)) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Controles de Densidad, Columnas y Movimiento
                    SlidersSection(
                        config = config,
                        onConfigChange = onConfigChange
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botones de Acción Rápida (Pausar, Reiniciar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onConfigChange(config.copy(isPaused = !config.isPaused)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x33FFFFFF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(if (config.isPaused) R.string.btn_resume else R.string.btn_pause),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Button(
                        onClick = onResetConfig,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x33FF2A2A),
                            contentColor = Color(0xFFFF8888)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.btn_reset),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
