# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/X/Work/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ==================================================================================================
# App Settings
# ==================================================================================================
-keep class com.x.android.app.glyphrecorder.component.** {
    *;
}

# ==================================================================================================
# Google Play Services
# ==================================================================================================
-dontwarn com.google.**
-keep class com.google.** {*;}
-keepclassmembers class com.google.** {*;}

# ==================================================================================================
# Android Settings
# ==================================================================================================
-keep class android.app.** {
    *;
}

# ==================================================================================================
# Crashlytics Settings
# ==================================================================================================
-dontwarn com.crashlytics.**
-keep class com.crashlytics.** {*;}
-keepclassmembers class com.crashlytics.** {*;}