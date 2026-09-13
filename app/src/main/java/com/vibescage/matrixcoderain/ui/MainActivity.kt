package com.vibescage.matrixcoderain.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.vibescage.matrixcoderain.audio.RainAudioManager
import com.vibescage.matrixcoderain.data.MatrixConfig
import com.vibescage.matrixcoderain.data.MatrixPreferences
import com.vibescage.matrixcoderain.service.MatrixWallpaperService
import com.vibescage.matrixcoderain.ui.components.CyberHudOverlay
import com.vibescage.matrixcoderain.ui.components.MessageHudOverlay
import com.vibescage.matrixcoderain.ui.theme.MatrixCodeRainTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferences: MatrixPreferences
    private lateinit var rainAudioManager: RainAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pantalla completa inmersiva
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        preferences = MatrixPreferences(applicationContext)
        rainAudioManager = RainAudioManager(applicationContext)

        setContent {
            MatrixCodeRainTheme {
                val currentConfig by preferences.configFlow.collectAsState(initial = MatrixConfig())
                val coroutineScope = rememberCoroutineScope()
                var isHudVisible by remember { mutableStateOf(false) }
                var isMessageHudVisible by remember { mutableStateOf(false) }
                var canvasViewRef by remember { mutableStateOf<MatrixCanvasView?>(null) }

                // Control del Audio según configuración
                LaunchedEffect(currentConfig.enableAudio) {
                    if (currentConfig.enableAudio) {
                        rainAudioManager.start()
                    } else {
                        rainAudioManager.stop()
                    }
                }

                // Ciclo de vida para pausar audio al salir
                DisposableEffect(Unit) {
                    onDispose {
                        rainAudioManager.stop()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Lienzo de Lluvia Matrix Interactivo
                    AndroidView(
                        factory = { ctx ->
                            MatrixCanvasView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                engine.config = currentConfig
                                canvasViewRef = this
                            }
                        },
                        update = { view ->
                            view.engine.config = currentConfig
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. HUD Cyberpunk Flotante (Configuración General - Tuerca en esquina inferior derecha)
                    CyberHudOverlay(
                        config = currentConfig,
                        isHudVisible = isHudVisible,
                        onToggleHud = {
                            isHudVisible = !isHudVisible
                            if (isHudVisible) isMessageHudVisible = false
                        },
                        onConfigChange = { newConfig ->
                            coroutineScope.launch {
                                preferences.saveConfig(newConfig)
                            }
                        },
                        onSetWallpaperClick = {
                            openLiveWallpaperPicker()
                        },
                        onResetConfig = {
                            coroutineScope.launch {
                                preferences.saveConfig(MatrixConfig())
                            }
                        }
                    )

                    // 3. Ventana Independiente de Mensajes en Pantalla (Redondel en esquina inferior izquierda)
                    MessageHudOverlay(
                        palette = currentConfig.palette,
                        isVisible = isMessageHudVisible,
                        onToggleVisible = {
                            isMessageHudVisible = !isMessageHudVisible
                            if (isMessageHudVisible) isHudVisible = false
                        },
                        onInjectMessage = { message ->
                            canvasViewRef?.engine?.injectMessage(message)
                        },
                        onClearMessage = {
                            canvasViewRef?.engine?.clearMessage()
                        }
                    )
                }
            }
        }
    }

    private fun openLiveWallpaperPicker() {
        val componentName = ComponentName(this, MatrixWallpaperService::class.java)
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, componentName)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback para versiones o launchers antiguos
            val fallbackIntent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
            try {
                startActivity(fallbackIntent)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        rainAudioManager.stop()
    }

    override fun onResume() {
        super.onResume()
        // Audio se reanuda automáticamente vía LaunchedEffect si está activo
    }

    override fun onDestroy() {
        super.onDestroy()
        rainAudioManager.release()
    }
}
