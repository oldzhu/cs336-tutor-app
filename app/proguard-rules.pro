# ProGuard rules for CS336 Tutor App

# Keep Chaquopy Python runtime
-keep class com.chaquo.python.** { *; }

# Keep Room entities
-keep class com.cs336.tutor.data.local.entity.** { *; }

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Retrofit
-keep class retrofit2.** { *; }
-keepattributes Exceptions
