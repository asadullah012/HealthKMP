package com.viktormykhailiv.kmp.health.sample.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Card Shell ────────────────────────────────────────────────────────────────
@Composable
fun VitalsCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable{
            onClick.invoke()
        },
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content  = { Column(modifier = Modifier.padding(20.dp), content = content) }
    )
}

// ── Heart Rate Bar Chart ──────────────────────────────────────────────────────
@Composable
fun HeartRateBarChart(
    readings: List<Int>,
    modifier: Modifier = Modifier
) {
    val maxVal = readings.maxOrNull()?.toFloat() ?: 1f
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(readings) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(900, easing = EaseOutCubic))
    }
    val progress by animProgress.asState()
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val labelHeight = 16.dp.toPx()
        val chartHeight = size.height - labelHeight
        val barCount    = readings.size
        val spacing     = 6.dp.toPx()
        val barWidth    = (size.width - spacing * (barCount - 1)) / barCount
        val highlight   = readings.indexOf(readings.maxOrNull())

        readings.forEachIndexed { i, value ->
            val heightRatio = (value / maxVal) * progress
            val barHeight   = chartHeight * heightRatio
            val left        = i * (barWidth + spacing)
            val top         = chartHeight - barHeight
            val color       = if (i == highlight) BarActive else BarInactive

            drawRoundRect(
                color        = color,
                topLeft      = Offset(left, top),
                size         = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )

            val textLayout = textMeasurer.measure(
                text = "$value",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = color,
                    textAlign = TextAlign.Center,
                ),
            )

            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x = left + (barWidth - textLayout.size.width) / 2,
                    y = chartHeight + (labelHeight - textLayout.size.height) / 2,
                ),
            )
        }
    }
}

// ── Circular Steps Progress ───────────────────────────────────────────────────
@Composable
fun StepsCircle(
    current: Int,
    goal: Int,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(percentage) {
        animProgress.animateTo(percentage, animationSpec = tween(1000, easing = EaseOutCubic))
    }
    val anim by animProgress.asState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke     = 10.dp.toPx()
            val inset      = stroke / 2
            val arcSize    = Size(size.width - stroke, size.height - stroke)
            val startAngle = -90f
            val sweepFull  = 360f

            // Background track
            drawArc(
                color      = TealLight,
                startAngle = startAngle,
                sweepAngle = sweepFull,
                useCenter  = false,
                topLeft    = Offset(inset, inset),
                size       = arcSize,
                style      = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                brush      = Brush.sweepGradient(listOf(TealPrimary, Color(0xFF80DEEA), TealPrimary)),
                startAngle = startAngle,
                sweepAngle = sweepFull * anim,
                useCenter  = false,
                topLeft    = Offset(inset, inset),
                size       = arcSize,
                style      = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = current.toString(),
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = TextPrimary
            )
            Text(
                text     = "GOAL: ${goal / 1000}",
                fontSize = 10.sp,
                color    = TextSecondary,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ── Horizontal Progress Bar ───────────────────────────────────────────────────
@Composable
fun AnimatedProgressBar(
    progress: Float,
    trackColor: Color  = OrangeLight,
    fillColor: Color   = OrangeAccent,
    height: Dp         = 8.dp,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animProgress.animateTo(progress, animationSpec = tween(900, easing = EaseOutCubic))
    }
    val anim by animProgress.asState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(anim)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        )
    }
}

// ── Sleep Progress Bar ────────────────────────────────────────────────────────
@Composable
fun SleepBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    AnimatedProgressBar(
        progress   = progress,
        trackColor = color.copy(alpha = 0.2f),
        fillColor  = color,
        height     = 6.dp,
        modifier   = modifier
    )
}

// ── Section Label ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        letterSpacing = 0.05.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

// ── Icon Badge ────────────────────────────────────────────────────────────────
@Composable
fun IconBadge(
    backgroundColor: Color,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier            = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment    = Alignment.Center,
        content             = content
    )
}