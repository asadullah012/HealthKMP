package com.viktormykhailiv.kmp.health.sample.presentation

import platform.UIKit.UIDevice

actual fun getPlatformName(): String {
    val device = UIDevice.currentDevice
    val systemName = device.systemName
    val systemVersion = device.systemVersion
    return "$systemName $systemVersion"
}