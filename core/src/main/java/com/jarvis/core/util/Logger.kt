package com.jarvis.core.util

import android.util.Log

object Logger {
    private const val TAG = "JARVIS"

    fun debug(message: String, error: Throwable? = null) {
        if (error != null) {
            Log.d(TAG, message, error)
        } else {
            Log.d(TAG, message)
        }
    }

    fun info(message: String, error: Throwable? = null) {
        if (error != null) {
            Log.i(TAG, message, error)
        } else {
            Log.i(TAG, message)
        }
    }

    fun warn(message: String, error: Throwable? = null) {
        if (error != null) {
            Log.w(TAG, message, error)
        } else {
            Log.w(TAG, message)
        }
    }

    fun error(message: String, error: Throwable? = null) {
        if (error != null) {
            Log.e(TAG, message, error)
        } else {
            Log.e(TAG, message)
        }
    }

    fun redact(text: String): String {
        val pattern = Regex("(?i)(password|token|api[_-]?key|secret)=\\s*\\S+")
        return pattern.replace(text, "$1=***REDACTED***")
    }
}
