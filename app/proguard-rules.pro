# Matrix Code Rain Android - Proguard / R8 rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
