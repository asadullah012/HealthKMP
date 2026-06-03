package com.viktormykhailiv.kmp.health.sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(4.dp))
            .appGradient()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            color = Color.White,
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 5f,
            )
        ),
    )
}
