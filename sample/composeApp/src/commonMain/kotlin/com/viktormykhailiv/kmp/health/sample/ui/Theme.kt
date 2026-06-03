package com.viktormykhailiv.kmp.health.sample.ui

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val TealLight      = Color(0xFFE1F5EE)
val TealMid        = Color(0xFF0F6E56)
val TealDark       = Color(0xFF085041)

val GreenLight     = Color(0xFFEAF3DE)
val GreenMid       = Color(0xFF3B6D11)

val AmberLight     = Color(0xFFFAEEDA)
val AmberMid       = Color(0xFFBA7517)
val AmberDark      = Color(0xFF854F0B)

val GrayLight      = Color(0xFFF1EFE8)
val GrayMid        = Color(0xFF5F5E5A)

val CardBg         = Color(0xFFFFFFFF)
val CardBorder     = Color(0x26000000)   // ~15 % black
val DividerColor   = Color(0x1A000000)
val TextPrimary    = Color(0xFF1C1B1F)
val TextSecondary  = Color(0xFF6B6B6B)
val DangerBorder   = Color(0x4D000000)
val TealPrimary      = Color(0xFF00BFA5)
val TealSurface      = Color(0xFFE0F7F5)
val OrangeAccent     = Color(0xFFFF8C00)
val OrangeLight      = Color(0xFFFFE0B2)
val PurpleAccent     = Color(0xFF7C4DFF)
val PurpleLight      = Color(0xFFEDE7F6)
val HeartRed         = Color(0xFFFF5252)
val HeartRedLight    = Color(0xFFFFEBEE)
val SleepPurple      = Color(0xFF7C4DFF)
val TextMuted        = Color(0xFF9CA3AF)
val ScreenBg         = Color(0xFFF5FAFA)
val BarActive        = Color(0xFF00BFA5)
val BarInactive      = Color(0xFFB2EBE7)

val HealthColorScheme = lightColorScheme(
    primary          = TealPrimary,
    onPrimary        = Color.White,
    primaryContainer = TealSurface,
    secondary        = OrangeAccent,
    background       = ScreenBg,
    surface          = CardBg,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
)

// ── Typography ────────────────────────────────────────────────────────────────
// Using system fonts as fallback; replace Font(...) with actual font resources
val HealthTypography = androidx.compose.material3.Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        color      = TextPrimary,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 24.sp,
        color      = TextPrimary,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        color      = TextPrimary
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        color      = TextSecondary,
        letterSpacing = 0.8.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        color      = TextPrimary
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        color      = TextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        color      = TextMuted,
        letterSpacing = 0.5.sp
    )
)
