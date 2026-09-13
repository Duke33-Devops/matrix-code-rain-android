# Plan de Trabajo: Matrix Code Rain Android

## 📌 Dashboard de Estado
- **Proyecto:** Matrix Code Rain Android (Nativo + Live Wallpaper + Screensaver)
- **Hito Actual:** Hito 1.0 — MVP Nativo, Live Wallpaper & Build Estable ✅
- **Estado Global:** Totalmente funcional, APKs Debug y Release firmados y subidos a Google Drive (100% completado)
- **Fase Activa:** Fase 5: Generación Release Firmado y Subida a Google Drive ✅
- **Bitácora Histórica:** [`BITACORA.md`](./BITACORA.md)

---

## 🛠️ Ficha Técnica y Mapa del Proyecto

| Componente | Tecnología / Ubicación | Rol Principal |
| :--- | :--- | :--- |
| **Motor Gráfico (Engine)** | Kotlin 2D Canvas en `engine/MatrixEngine.kt` | Renderizado ultra-optimizado a 60-120 FPS sin GC churn, cuadrícula estática, estelas y bloom neón. |
| **Live Wallpaper** | Android `WallpaperService` en `service/MatrixWallpaperService.kt` | Fondo de pantalla animado para Home y Lockscreen con bajo consumo energético al apagarse la pantalla. |
| **Salvapantallas (Daydream)** | Android `DreamService` en `service/MatrixDreamService.kt` | Modo salvapantallas interactivo al estar en base de carga o reposo. |
| **Interfaz HUD Flotante** | Jetpack Compose + Material 3 en `ui/` | Panel Glassmorphism Cyberpunk para calibrar columnas, densidad, velocidad, colores y mensajes. |
| **Motor de Audio** | Android `MediaPlayer` en `audio/RainAudioManager.kt` | Bucle continuo de lluvia binaural con fundidos suaves (*fade-in* y *fade-out*). |
| **Persistencia** | Jetpack DataStore Preferences en `data/MatrixPreferences.kt` | Almacenamiento reactivo de parámetros compartido entre Activity y WallpaperService. |
| **Iconografía** | Mipmaps PNG + Adaptive Vector + `google-play-icon.png` | Iconos HD adaptativos y recurso 512x512 para Google Play Store. |
| **Distribución Google Drive** | Google Drive for Desktop (`H:\Mi unidad`) | Sincronización en la nube de binarios APK Debug y Release firmados. |

### Comandos de Ejecución y Validación
- **Compilación APK Debug:** `.\gradlew assembleDebug`
- **Compilación APK Release Firmado:** `.\gradlew assembleRelease`
- **Instalación en Dispositivo/Emulador:** `.\gradlew installDebug` o `.\gradlew installRelease`
- **Inspección de Dispositivos Conectados:** `& "C:\Users\ferna\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices`
- **Ubicación de APKs en Google Drive:** `H:\Mi unidad\matrix-code-rain-release.apk` y `matrix-code-rain-debug.apk`

---

## 🗺️ Roadmap y Estado de las Fases

```mermaid
graph TD
    A[Fase 1: Motor Gráfico Canvas MatrixEngine] --> B[Fase 2: Servicios Wallpaper & Daydream]
    B --> C[Fase 3: HUD Jetpack Compose y Audio]
    C --> D[Fase 4: Setup Gradle, Iconos HD y Validación APK]
    D --> E[Fase 5: Generación Release Firmado y Subida a Google Drive]
    E --> F[Fase 6: Ventana Independiente de Mensajes y Botón Quitar Mensaje]
```

### Resumen de Fases Completadas (Ver detalle en BITACORA.md)
- *Fase 1: Motor Gráfico Canvas MatrixEngine:* Render 2D en `MatrixEngine.kt` con glifos auténticos, modo cuadrícula fija y desplazamiento dinámico. ✅
- *Fase 2: Servicios Wallpaper & Daydream:* Implementación de `MatrixWallpaperService` y `MatrixDreamService` registrados en el AndroidManifest. ✅
- *Fase 3: HUD Jetpack Compose y Audio:* Interfaz `CyberHudOverlay`, 6 paletas cyberpunk, inyector de mensajes y sonido binaural `RainAudioManager`. ✅
- *Fase 4: Setup Gradle, Iconos HD y Validación APK:* Configuración de `gradle.properties`, corrección de tipos en `SlidersSection.kt`, generación de mipmaps y compilación exitosa del APK. ✅
- *Fase 5: Generación Release Firmado y Subida a Google Drive:* Compilación con R8/Proguard (16.3 MB) y subida a `H:\Mi unidad`. ✅
- *Fase 6: Ventana Independiente de Mensajes:* Creación de `MessageHudOverlay`, redondel en esquina inferior izquierda, elevación del texto en pantalla y botón para quitar el mensaje activo. ✅

---

## 🎯 Fase Activa: Fase 6: Ventana Independiente de Mensajes y Botón Quitar Mensaje ✅

### Objetivo
Separar la configuración de mensajes a una ventana propia minimizable en la esquina inferior izquierda y proveer un botón para limpiar el mensaje activo del lienzo.

### Criterios de Aceptación
- Redondel en la esquina inferior izquierda para abrir/cerrar la ventana de mensajes.
- Redondel de la tuerca en la esquina inferior derecha para la configuración general.
- Botón "Quitar Mensaje de Pantalla" que borra instantáneamente el texto decodificado.
- Posición del texto elevada (`centerY = height * 0.36f`) para una lectura clara sin obstrucción.
- Binarios actualizados (`matrix-code-rain-release.apk` y `matrix-code-rain-debug.apk`) disponibles en `H:\Mi unidad\`.

---

## 🚀 Cómo Continuar en la Siguiente Conversación

Al abrir una nueva conversación, copia y pega el siguiente mensaje:

```
Continúa con el PLAN_DE_TRABAJO.md de e:/Antigravity/Matrix code rain Android.
La Fase 6 (Ventana independiente de mensajes, botón de borrado y APKs actualizados en Google Drive) está completada.
Procede de forma 100% autónoma para cualquier nueva solicitud.
```
