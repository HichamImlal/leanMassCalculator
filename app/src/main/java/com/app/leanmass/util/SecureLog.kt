package com.app.leanmass.util

import android.util.Log

/**
 * MASVS-STORAGE-2: Utility to ensure logs are only printed in Debug mode.
 * In a real production app, we use BuildConfig.DEBUG.
 */
object SecureLog {
    private const val IS_DEBUG_ENABLED = true // Change to false for Production (MASVS compliance)

    fun d(tag: String, message: String) {
        if (IS_DEBUG_ENABLED) {
            Log.d(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (IS_DEBUG_ENABLED) {
            Log.e(tag, message, throwable)
        }
    }

    fun i(tag: String, message: String) {
        if (IS_DEBUG_ENABLED) {
            Log.i(tag, message)
        }
    }
}
