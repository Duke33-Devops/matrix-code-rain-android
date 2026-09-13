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

---

### [2026-09-13] — Fase 5: Generación Release Firmado y Subida a Google Drive ✅
- **Objetivo:** Generar la versión de producción optimizada con R8/Proguard, habilitar la firma de release y subir ambos binarios (Debug y Release) a la unidad en la nube de Google Drive del usuario.
- **Implementación:**
  - Configuración de `signingConfig = signingConfigs.getByName("debug")` para el buildType `release` en `app/build.gradle.kts`.
  - Compilación exitosa de `app-release.apk` (16.37 MB vs 32.57 MB de la versión debug, 50% de reducción de tamaño).
  - Detección del cliente local de Google Drive en `H:\Mi unidad`.
  - Copia y sincronización de `matrix-code-rain-release.apk` y `matrix-code-rain-debug.apk` en `H:\Mi unidad\`.
- **Decisión de Diseño (Mini-ADR 5):**
  - *Decisión:* Subir tanto la versión Release optimizada (16.3 MB) como la versión Debug (32.5 MB) con nombres descriptivos.
  - *Motivo:* Permite al usuario instalar inmediatamente la versión más ligera y fluida en cualquier dispositivo personal, manteniendo la versión debug por si se requieren logs de desarrollo mediante Logcat o depuración por cable.
- **Archivos Clave:** [`app/build.gradle.kts`], [`H:\Mi unidad\matrix-code-rain-release.apk`], [`H:\Mi unidad\matrix-code-rain-debug.apk`]

---

### [2026-09-13] — Fase 6: Ventana Independiente de Mensajes y Función de Borrado de Pantalla ✅
- **Objetivo:** Resolver el problema en el que el mensaje proyectado en pantalla no podía eliminarse una vez mostrado y quedaba oculto detrás de la ventana de configuración, separando la gestión de mensajes en una ventana independiente minimizable a un redondel en la esquina inferior izquierda.
- **Implementación:**
  - `MatrixEngine.kt`:
    - Implementación del método `clearMessage()` que restablece `terminalText = null` y limpia los contadores de renderizado.
    - Ajuste de elevación de la posición vertical `centerY = height * 0.36f` (tercio superior) para que el mensaje no colisione con ventanas o controles inferiores.
  - Creación de `MessageHudOverlay.kt`:
    - Botón flotante circular en la esquina inferior izquierda (`Alignment.BottomStart`, 54.dp, borde neón cyberpunk e icono `Icons.AutoMirrored.Filled.Chat`).
    - Panel flotante Glassmorphism independiente con campo de texto, botón Enviar y frases predefinidas.
    - Botón destacado de borrado ("Quitar Mensaje de Pantalla") con estilo de acento de advertencia.
  - `CyberHudOverlay.kt`:
    - Desacoplamiento de la sección de mensajes del menú general de configuración, reduciendo la altura de la ventana y mejorando la ergonomía.
  - `MainActivity.kt`:
    - Gestión de estados de visibilidad independientes (`isHudVisible` para la tuerca y `isMessageHudVisible` para el redondel de mensajes).
  - Actualización de cadenas multiidioma en `values/strings.xml` y `values-es/strings.xml`.
  - Recompilación y despliegue directo de `matrix-code-rain-debug.apk` y `matrix-code-rain-release.apk` en `H:\Mi unidad\`.
- **Decisión de Diseño (Mini-ADR 6):**
  - *Decisión:* Separar la interfaz en dos accesos directos minimizables simétricos (Tuerca a la derecha para ajustes del motor gráfico, Burbuja de chat a la izquierda para mensajes).
  - *Motivo:* Permite al usuario tener un control táctil limpio en pantallas móviles tipo 9:16 o 20:9 con una sola mano, sin abarrotar la pantalla ni tapar el texto de la terminal decodificada.
- **Archivos Clave:** [`MatrixEngine.kt`], [`MessageHudOverlay.kt`], [`CyberHudOverlay.kt`], [`MainActivity.kt`], [`H:\Mi unidad\matrix-code-rain-release.apk`]

