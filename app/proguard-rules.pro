# Сохраняем все классы, кроме com.google.android.material и androidx
-keep class !com.google.android.material.**,!androidx.** { *; }

# Сжимаем и оптимизируем только классы com.google.android.material и androidx
-keepclassmembers class com.google.android.material.** { *; }
-keepclassmembers class androidx.** { *; }

# Игнорируем предупреждения для этих библиотек
-dontwarn com.google.android.material.**
-dontwarn androidx.**
