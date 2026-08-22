# 🟢 Matrix Code Rain Android

Aplicación nativa de **The Matrix Code Rain** para Android, diseñada con **Kotlin**, **Jetpack Compose** y un motor de renderizado 2D/Canvas ultra-optimizado a 60/120 FPS.

Incluye soporte para **Fondo de Pantalla Animado (Live Wallpaper)**, **Salvapantallas (DreamService)**, personalización avanzada de parámetros, efectos visuales Cyberpunk y sonido binaural de lluvia en bucle continuo.

---

## ✨ Características Principales

1. **🟢 Live Wallpaper Nativo (`WallpaperService`)**:
   - Configura la lluvia de código Matrix directamente como fondo de pantalla de inicio y bloqueo.
   - Máxima eficiencia energética: se pausa automáticamente cuando la pantalla está apagada o cubierta.

2. **⚡ Modo Cuadrícula Estática vs. Desplazamiento Fluido**:
   - **Cuadrícula Estática**: Los caracteres Katakana y Cyberpunk permanecen fijos en su celda y la gota avanza iluminándolos secuencialmente.
   - **Desplazamiento Fluido**: Movimiento continuo y dinámico en cascada.

3. **💡 Resplandor Neón en Rastro Calibrado (0% a 100%)**:
   - Ajustable en pasos de 10%: de 1 letra (0%) hasta 11 letras (100%) con degradado de glow descendente.

4. **🌧️ Caudal / Intensidad de Lluvia y Columnas**:
   - Selector exacto de columnas para cualquier densidad de pantalla.
   - Gotas máximas simultáneas por columna (1 a 6).
   - Caudal de lluvia regulable con tope al 75%.

5. **🎨 6 Paletas de Color Cyberpunk**:
   - 🟢 Verde Matrix Clásico
   - 🔵 Cyber Cyan
   - 🟠 Ámbar CRT Retro
   - 🟣 Neon Synthwave
   - 🔴 Rojo Brecha / Intrusión
   - ⚪ Blanco Puro Monocromático

6. **🌧️ Paisaje Sonoro de Lluvia (`RainAudioManager`)**:
   - Reproducción en bucle con transiciones suaves (fade-in / fade-out).

7. **📱 Interfaz Moderna con Jetpack Compose**:
   - Panel HUD translúcido (Glassmorphism), accesible y táctil.
   - Inyección de mensajes terminal ("Wake up, Neo...", frases personalizadas).

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje**: Kotlin 2.x
- **UI Toolkit**: Jetpack Compose + Material 3
- **Motor Gráfico**: Motor 2D Canvas optimizado (`MatrixEngine`) desacoplado para uso en `Activity`, `WallpaperService` y `DreamService`.
- **Persistencia**: Jetpack DataStore Preferences
- **Audio**: MediaPlayer nativo con audio loop en `res/raw`
- **Idiomas soportados**: Español (es), Inglés (en), Ruso (ru), Chino (zh)
- **Compatibilidad**: Android 8.0+ (API 26+) hasta Android 15+ (API 35+)

---

## 🚀 Compilación y Ejecución

Para compilar y generar el APK:

```bash
./gradlew assembleDebug
```

Para instalarlo en un dispositivo o emulador conectado con ADB:

```bash
./gradlew installDebug
```
