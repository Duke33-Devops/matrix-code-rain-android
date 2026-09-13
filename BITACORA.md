# Bitácora de Desarrollo — Matrix Code Rain Android

Este documento registra cronológicamente todas las fases, decisiones técnicas de diseño (Mini-ADRs), cambios implementados y referencias de contexto de la versión nativa de Matrix Code Rain para Android.

---

## 📖 Entradas Cronológicas y Decisiones de Diseño (Mini-ADRs)

### [2026-09-13] — Fase 1: Arquitectura Base y Motor Canvas MatrixEngine ✅
- **Objetivo:** Implementar el motor gráfico 2D Canvas nativo con glifos auténticos Katakana y Cyberpunk, asegurando una tasa de refresco fluida a 60/120 FPS sin latencias de recolección de basura (*GC churn*).
- **Implementación:**
  - Creación de `MatrixEngine.kt`: Lógica desacoplada de la vista para poder ser consumida indistintamente por `MatrixCanvasView`, `WallpaperService` o `DreamService`.
  - Reutilización estricta de objetos `Paint` (`textPaint`, `glowPaint`, `fadePaint`, `crtPaint`, `shockwavePaint`).
  - Bitmap persistente `trailBitmap` para acumulación suave del desvanecimiento (*fadeOpacity*).
- **Decisión de Diseño (Mini-ADR 1):**
  - *Decisión:* Separar por completo la lógica matemática y de dibujo en una clase pura de Kotlin (`MatrixEngine`) sin ligarla directamente a un ciclo de vida de View o Activity.
  - *Motivo:* Permite que el fondo de pantalla animado (`WallpaperService`) pinte directamente sobre el `SurfaceHolder` en un hilo de fondo o Looper sin necesidad de inflar vistas de Compose ni crear dependencias circulares.
- **Archivos Clave:** [`app/src/main/java/com/vibescage/matrixcoderain/engine/MatrixEngine.kt`], [`app/src/main/java/com/vibescage/matrixcoderain/ui/MatrixCanvasView.kt`]

---

### [2026-09-13] — Fase 2: Servicios de Sistema Android (Live Wallpaper y DreamService) ✅
- **Objetivo:** Proporcionar soporte nativo para que el usuario pueda configurar la lluvia de Matrix como Fondo de Pantalla Animado en su pantalla de inicio / bloqueo y como Salvapantallas (Daydream) al cargar el dispositivo.
- **Implementación:**
  - Creación de `MatrixWallpaperService.kt` extendiendo `WallpaperService`.
  - Detección de visibilidad con `onVisibilityChanged`: cuando la pantalla se apaga o se abre otra app a pantalla completa, se cancelan los callbacks del `Handler` para consumir 0% de batería.
  - Implementación de `MatrixDreamService.kt` con interactividad y pantalla completa.
  - Declaración de permisos `SET_WALLPAPER`, `BIND_WALLPAPER` y `BIND_DREAM_SERVICE` en `AndroidManifest.xml`.
- **Decisión de Diseño (Mini-ADR 2):**
  - *Decisión:* Suscripción reactiva mediante Coroutines y Flow (`prefs.configFlow.collectLatest`) dentro de los servicios.
  - *Motivo:* Cuando el usuario ajusta colores, velocidad o densidad en el HUD de la app principal, el fondo de pantalla animado actualiza sus parámetros inmediatamente sin necesidad de reiniciar el servicio ni recargar la app.
- **Archivos Clave:** [`app/src/main/java/com/vibescage/matrixcoderain/service/MatrixWallpaperService.kt`], [`app/src/main/java/com/vibescage/matrixcoderain/service/MatrixDreamService.kt`]

---

### [2026-09-13] — Fase 3: HUD Jetpack Compose, Paletas Cyberpunk y Audio Inmersivo ✅
- **Objetivo:** Diseñar un panel de control translúcido táctil (Glassmorphism), selector de 6 esquemas de color, inyector de mensajes de terminal y sonido binaural de lluvia.
- **Implementación:**
  - `CyberHudOverlay.kt`: Panel flotante animado con `AnimatedVisibility`, botón de acceso rápido minimizable y acceso directo a la selección de Live Wallpaper del sistema.
  - `PaletteSelector.kt` y `MatrixPalette.kt`: 6 paletas icónicas (Matrix Green, Cyber Cyan, CRT Amber, Synthwave, Breach Red y Pure White).
  - `RainAudioManager.kt`: Reproductor en bucle sobre `res/raw/lluvia.m4a` con interpolación de volumen (*fade-in* / *fade-out*) en pasos de delay para evitar chasquidos de audio.
- **Decisión de Diseño (Mini-ADR 3):**
  - *Decisión:* Manejo de sonido no bloqueante y acoplado al ciclo de vida de la Activity (`onPause`/`onResume`).
  - *Motivo:* Evita que el audio de lluvia siga sonando accidentalmente en segundo plano cuando la app principal se minimiza, reservando el sonido para el uso explícito en la pantalla de simulación.
- **Archivos Clave:** [`app/src/main/java/com/vibescage/matrixcoderain/ui/components/CyberHudOverlay.kt`], [`app/src/main/java/com/vibescage/matrixcoderain/audio/RainAudioManager.kt`]

---

### [2026-09-13] — Fase 4: Setup Gradle, Iconos HD y Validación APK ✅
- **Objetivo:** Resolver dependencias de compilación en el entorno local (Android SDK, AndroidX), corregir tipos de Jetpack Compose en `SlidersSection.kt`, generar densidades mipmap y compilar el binario APK.
- **Implementación:**
  - Creación de `local.properties` apuntando al SDK de Android local (`C:\Users\ferna\AppData\Local\Android\Sdk`).
  - Creación de `gradle.properties` con `android.useAndroidX=true` y `android.nonTransitiveRClass=true`.
  - Corrección de tipo en `SlidersSection.kt`: `SwitchDefaults` sustituido por `SwitchColors` en `ToggleRow`.
  - Generación de iconos adaptativos en `mipmap-mdpi` (48px), `hdpi` (72px), `xhdpi` (96px), `xxhdpi` (144px), `xxxhdpi` (192px) y preservación del icono 512x512 para Google Play Store a partir de `google-play-icon.png`.
  - Compilación exitosa: `app-debug.apk` (32.1 MB) generado en `app/build/outputs/apk/debug/`.
- **Decisión de Diseño (Mini-ADR 4):**
  - *Decisión:* Proveer tanto iconos vectoriales adaptativos (`mipmap-anydpi-v26`) como mapas de bits (`mipmap-*dpi`) de alta definición.
  - *Motivo:* Garantiza máxima nitidez visual en cualquier versión de Android (desde Android 8 hasta Android 15), previniendo que launchers de terceros o versiones previas a API 26 muestren el icono genérico por defecto.
- **Archivos Clave:** [`gradle.properties`], [`app/src/main/java/com/vibescage/matrixcoderain/ui/components/SlidersSection.kt`], [`google-play-icon.png`]
