package com.mariyer.cookbook.utils

import android.icu.util.VersionInfo
import android.util.Log
import com.mariyer.cookbook.BuildConfig

fun debug(tag: String?, message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}