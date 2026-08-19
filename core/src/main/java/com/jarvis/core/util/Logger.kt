package com.jarvis.core.util

import android.util.Log

object Logger {
    private const val TAG = "JARVIS"

    fun debug(msg: String) {
        Log.d(TAG, "[DEBUG] $msg")
    }

    fun info(msg: String) {
        Log.i(TAG, "[INFO] $msg")
    }

    fun warn(msg: String) {
        Log.w(TAG, "[WARN] $msg")
    }

    fun error(msg: String, throwable: Throwable? = null) {
        Log.e(TAG, "[ERROR] $msg", throwable)
    }

    fun redact(text: String): String {
        return text.replace(Regex("(?i)(password|token|api[_-]?key|secret)\\s*[=:]\s*\\S+"), "$1=***REDACTED***")
    }
}
