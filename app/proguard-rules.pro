# MASVS-STORAGE-2: Remove sensitive logs in production
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep SQLCipher classes
-keep class net.zetetic.database.sqlcipher.** { *; }

# Keep Room classes
-keep class androidx.room.** { *; }
