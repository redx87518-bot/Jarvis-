# proguard rules for JARVIS
-keep class com.jarvis.core.** { *; }
-keep class com.jarvis.data.** { *; }
-keep class com.jarvis.android.** { *; }
-keep class com.jarvis.launcher.** { *; }

# Kotlinx serialization
-keepattributes Signature
-keepattributes *Annotation*
-keepclasseswithmemberclassmembers class kotlinx.** {
    *;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.paging.**

# Keep Hilt
-keep class dagger.hilt.**
-keep class javax.inject.**
-keep class androidx.hilt.**
-keepnames class com.jarvis.launcher.JarvisApplication

# Suppress warnings
-dontwarn kotlinx.coroutines.debug.**
-dontwarn kotlinx.coroutines.internal.**
-dontwarn kotlin.Metadata
