# Keep generic/signature metadata used by Gson for reflection on nested/generic types.
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# Keep API DTO/domain model fields so Gson can map JSON keys reliably in minified release builds.
-keepclassmembers class com.taj.portfolio.data.** {
    <fields>;
}

# Keep model classes and constructors referenced by Gson/Retrofit.
-keep class com.taj.portfolio.data.** {
    <init>(...);
}
