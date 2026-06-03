package com.viktormykhailiv.kmp.health.sample.ui

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.roundTo(n: Int): Double {
    val factor = 10.0.pow(n)
    return (this * factor).roundToInt() / factor
}