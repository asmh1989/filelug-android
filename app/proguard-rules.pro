-optimizationpasses 5
#-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

#your package path where models are stored
#-keep class your.package.name.here.** { *; }

-keep class com.filelug.android.service.NotificationConfig {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keep class com.filelug.android.service.FileToDownload {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keep class com.filelug.android.service.FileToUpload {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keep class com.filelug.android.service.NameValue {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keep class com.filelug.android.ui.model.LocalFileObject {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keep class com.filelug.android.ui.model.RemoteFileObject {
    public static final android.os.Parcelable$Creator CREATOR;
}

# The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle.
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/propagation/parameter
-keepattributes SourceFile,LineNumberTable,InnerClasses

##########################
## SUPPORT LIBRARIES    ##
##########################

# Ignore overridden Android classes
-dontwarn android.**
-keep class android.** { *; }
-keep interface android.** { *; }

# support
-dontwarn android.support.**
-keep class android.support.** { *; }

# support-v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }

# support-v13
-dontwarn android.support.v13.**
-keep class android.support.v13.** { *; }

#Google play services proguard rules:
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#
#-keep @interface com.google.android.gms.common.annotation.KeepName
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}
#
### because google play service gets stripped of unnessecary methods and code dont warn
#-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }

-keep class com.google.firebase.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all public constructors of all public classes, but still obfuscate+optimize their content.
# This is necessary because optimization removes constructors which are called through XML.
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep serializable objects
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##########################
## SECURITY             ##
##########################

# Remove VERBOSE and DEBUG level log statements
-assumenosideeffects class android.util.Log {
	public static *** isLoggable(...);
    public static *** i(...);
    public static *** d(...);
    public static *** v(...);
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
}

##########################
## LIBRARIES            ##
##########################

## LIBRARY: Volley
-dontwarn org.apache.http.**
-dontwarn com.android.volley.toolbox.**
-keep class com.android.volley.** { *; }

## LIBRARY: MaterialDialogs
-keep class com.afollestad.materialdialogs.** { *; }

## LIBRARY: ProcessButton
-keep class com.dd.processbutton.** { *; }

## LIBRARY: FloatingLabel
-keep class com.marvinlabs.widget.floatinglabel.** { *; }

## LIBRARY: FloatingActionButton
-keep class com.melnykov.fab.** { *; }

## LIBRARY: NineOldAndroids
-keep class com.nineoldandroids.** { *; }

## LIBRARY: UniversalImageLoader
-keep class com.nostra13.universalimageloader.** { *; }

## LIBRARY: MaterialProgressbar
-keep class me.zhanghai.android.materialprogressbar.** { *; }

## LIBRARY: Facebook Account Kit
-keep class com.facebook.accountkit.** { *; }

## LIBRARY: Zxing
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }
