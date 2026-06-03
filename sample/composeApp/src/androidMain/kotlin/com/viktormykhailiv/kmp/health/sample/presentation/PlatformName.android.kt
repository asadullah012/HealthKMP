package com.viktormykhailiv.kmp.health.sample.presentation

import android.os.Build

actual fun getPlatformName(): String = "Android ${Build.VERSION.SDK_INT}"