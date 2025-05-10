# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn kotlin.**
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }
-keepclassmembernames class kotlinx.coroutines.flow.internal.ChannelFlow {
  kotlinx.coroutines.channels.ReceiveChannel produceImpl(kotlinx.coroutines.CoroutineScope);
}
-keep class kotlin.coroutines.jvm.internal.SuspendLambda { *; }
-keep class kotlin.coroutines.jvm.internal.ContinuationImpl { *; }
-keepclassmembers class kotlin.coroutines.jvm.internal.ContinuationImpl {
    <fields>;
    <init>(...);
}

# Kotlinx Serialization
-keepnames class kotlinx.serialization.Serializable
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep class kotlinx.serialization.internal.*Serializer { *; }
-keep class kotlinx.serialization.PolymorphicSerializer { *; }
-keep class kotlinx.serialization.SealedClassSerializer { *; }

-keep class com.aokaze.anima.data.entities.** { *; }
-keepclassmembers class com.aokaze.anima.data.entities.** { *; }

# Hilt
-keepclassmembers class * {
    @dagger.hilt.android.internal.managers.ActivityRetainedComponentManager$ActivityRetainedLifecycleEntryPoint *;
    @dagger.hilt.InstallIn *;
    @dagger.hilt.EntryPoint *;
    @dagger.hilt.android.EntryPointAccessors *;
    @javax.inject.Inject <init>(...);
}
-keep class * {
    @dagger.hilt.android.HiltAndroidApp *;
}
-keep @interface dagger.hilt.android.WithFragmentBindings
-keep @interface dagger.hilt.DefineComponent
-keep @interface dagger.hilt.components.SingletonComponent
-keep @interface dagger.hilt.android.components.ActivityComponent
-keep @interface dagger.hilt.android.components.ActivityRetainedComponent
-keep @interface dagger.hilt.android.components.FragmentComponent
-keep @interface dagger.hilt.android.components.ServiceComponent
-keep @interface dagger.hilt.android.components.ViewComponent
-keep @interface dagger.hilt.android.components.ViewWithFragmentComponent
-keep @interface dagger.hilt.android.scopes.ActivityScoped
-keep @interface dagger.hilt.android.scopes.FragmentScoped
-keep @interface dagger.hilt.android.scopes.ServiceScoped
-keep @interface dagger.hilt.android.scopes.ViewScoped
-keep @interface dagger.hilt.android.scopes.ActivityRetainedScoped
-keep @interface javax.inject.Scope
-keep class javax.inject.Singleton

# Room
-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.Entity *;
    @androidx.room.Dao *;
    @androidx.room.Database *;
    @androidx.room.TypeConverter *;
    @androidx.room.Embedded *;
    @androidx.room.Relation *;
}
-keep class com.aokaze.anima.data.entities.Resume { *; }
-keep class com.aokaze.anima.data.dao.ResumeDao { *; }


# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**


# Enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}