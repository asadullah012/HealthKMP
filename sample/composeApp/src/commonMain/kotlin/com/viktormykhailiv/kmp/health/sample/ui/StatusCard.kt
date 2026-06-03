package com.viktormykhailiv.kmp.health.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShieldMoon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viktormykhailiv.kmp.health.sample.presentation.AuthStatus
import com.viktormykhailiv.kmp.health.sample.presentation.HomeUiState


// ─── Public entry-point composable ───────────────────────────────────────────

/**
 * Shows the correct health-manager status card based on [state].
 *
 * @param state          Current permission / availability state.
 * @param lastSyncLabel  Human-readable label for the last sync time, e.g. "2m ago".
 *                       Only shown in [PARTIAL_PERMISSION] and [FULLY_GRANTED] states.
 * @param onRequestReadPermission        Called when the user taps "Request read permission".
 * @param onRequestBackgroundPermission  Called when the user taps "Request background read".
 * @param onRevokePermission             Called when the user taps "Revoke permission".
 */
@Composable
fun HealthManagerCard(
    state: HomeUiState,
    lastSyncLabel: String = "2m ago",
    onRequestReadPermission: () -> Unit = {},
    onRequestBackgroundPermission: (() -> Unit)? = {},
    onRequestFullPermission: (() -> Unit)? = {},
    onRevokePermission: (() -> Unit)? = {},
) {
    if(state is HomeUiState.HealthNotAvailable){
        UnavailableCard()
    } else if(state is HomeUiState.Ready){
        if(state.backgroundSyncSupported){
            if(state.foregroundAuth == AuthStatus.Authorized && state.backgroundAuth == AuthStatus.Authorized){
                FullyGrantedCard(lastSyncLabel, onRevokePermission)
            } else if(state.foregroundAuth == AuthStatus.Authorized){
                ForeGroundPermissionOnlyCard(lastSyncLabel, onRequestBackgroundPermission, onRevokePermission)
            } else{
                AvailableNoPermissionCard(onRequestReadPermission, onRequestFullPermission)
            }
        } else {
            if (state.foregroundAuth == AuthStatus.Authorized) {
                FullyGrantedCard(lastSyncLabel, onRevokePermission)
            } else {
                AvailableNoPermissionCard(onRequestReadPermission, null)
            }
        }
    }
}

// ─── State 1 — Available, no permission ──────────────────────────────────────

@Composable
private fun AvailableNoPermissionCard(
    onRequestRead: () -> Unit,
    onRequestFullPermission: (() -> Unit)?,
) {
    HmCard {
        HmCardHeader(
            icon = Icons.Outlined.MonitorHeart,
            iconTint = TealMid,
            iconBg = TealLight,
            title = "Personal Health Sync",
            subtitle = "System ready for sync",
            badge = { ActiveBadge() },
        )

        HmDivider()

        SectionLabel("Grant permission")

        HmPrimaryButton(
            icon = Icons.Outlined.Shield,
            label = "Request read permission",
            onClick = onRequestRead,
        )
        if(onRequestFullPermission != null){
            Spacer(Modifier.height(8.dp))

            HmSecondaryButton(
                icon = Icons.Outlined.Schedule,
                label = "Request read and background read",
                onClick = onRequestFullPermission,
            )
        }
    }
}

// ─── State 2 — Partial permission ────────────────────────────────────────────

@Composable
private fun ForeGroundPermissionOnlyCard(
    lastSyncLabel: String,
    onRequestBackground: (() -> Unit)?,
    onRevokeRead: (() -> Unit)?,
) {
    HmCard {
        HmCardHeader(
            icon = Icons.Outlined.ShieldMoon,   // "half-shield" feel
            iconTint = AmberDark,
            iconBg = AmberLight,
            title = "Permission partially granted",
            subtitle = "Some permissions are missing",
            badge = { PartialBadge() },
        )

        HmDivider()

        SectionLabel("Permission status")

        PermissionStatusRow(
            icon = Icons.Outlined.Shield,
            iconTint = GreenMid,
            label = "Read permission",
            pill = { GrantedPill() },
        )

        HorizontalDivider(
            color = DividerColor,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 0.dp),
        )

        PermissionStatusRow(
            icon = Icons.Outlined.Schedule,
            iconTint = AmberMid,
            label = "Background read",
            pill = { NotGrantedPill() },
        )

        if(onRequestBackground != null){
            Spacer(Modifier.height(16.dp))

            HmPrimaryButton(
                icon = Icons.Outlined.Schedule,
                label = "Request background read",
                onClick = onRequestBackground,
            )
        }

        if(onRevokeRead != null){
            Spacer(Modifier.height(8.dp))

            HmDangerButton(
                icon = Icons.Outlined.Close,
                label = "Revoke read permission",
                onClick = onRevokeRead,
            )
        }
    }
}

// ─── State 3 — Fully granted ─────────────────────────────────────────────────

@Composable
private fun FullyGrantedCard(
    lastSyncLabel: String,
    onRevoke: (() -> Unit)?,
) {
    HmCard {
        HmCardHeader(
            iconSlot = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GreenLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = GreenMid,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            title = "Permission granted",
            subtitle = "Syncing health data",
            badge = { GrantedBadge() },
        )

        HmDivider()

        PermissionStatusRow(
            icon = Icons.Outlined.Shield,
            iconTint = GreenMid,
            label = "Read permission",
            pill = { ActiveSmallPill() },
        )

        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

        PermissionStatusRow(
            icon = Icons.Outlined.Schedule,
            iconTint = GreenMid,
            label = "Background read",
            pill = { ActiveSmallPill() },
        )

        if(onRevoke != null){
            Spacer(Modifier.height(16.dp))

            HmDangerButton(
                icon = Icons.Outlined.Close,
                label = "Revoke permission",
                onClick = onRevoke,
            )
        }

    }
}

// ─── State 4 — Unavailable ───────────────────────────────────────────────────

@Composable
private fun UnavailableCard() {
    HmCard {
        HmCardHeader(
            icon = Icons.Outlined.MonitorHeart,
            iconTint = GrayMid,
            iconBg = GrayLight,
            title = "Personal Health Sync",
            subtitle = "Health manager not available",
            badge = { UnavailableBadge() },
        )

        HmDivider()

        // Info box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F0))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(17.dp).padding(top = 1.dp),
            )
            Text(
                text = "Health manager is not supported on this device or OS version. " +
                        "A compatible device is required to sync health data.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 19.sp,
            )
        }

        Spacer(Modifier.height(14.dp))

        // Disabled button
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color(0xFFF1EFE8),
                disabledContentColor = Color(0xFFB4B2A9),
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Request permission", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Shared layout primitives ────────────────────────────────────────────────

@Composable
private fun HmCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

/** Header with a simple icon box. */
@Composable
private fun HmCardHeader(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: @Composable () -> Unit,
) {
    HmCardHeader(
        iconSlot = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
        },
        title = title,
        subtitle = subtitle,
        badge = badge,
    )
}

/** Header that accepts a custom icon slot (e.g. circle check). */
@Composable
private fun HmCardHeader(
    iconSlot: @Composable () -> Unit,
    title: String,
    subtitle: String,
    badge: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            iconSlot()
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(subtitle, fontSize = 13.sp, color = TextSecondary)
            }
        }
        Spacer(Modifier.width(8.dp))
        badge()
    }
}

@Composable
private fun HmDivider() {
    HorizontalDivider(
        color = DividerColor,
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 16.dp),
    )
}

@Composable
private fun PermissionStatusRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    pill: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F0))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        Text(label, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        pill()
    }
}

// ─── Buttons ─────────────────────────────────────────────────────────────────

@Composable
private fun HmPrimaryButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TealMid, contentColor = Color.White),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun HmSecondaryButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, TealMid.copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TealMid),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun HmDangerButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DangerBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Badges ───────────────────────────────────────────────────────────────────

@Composable
private fun ActiveBadge() = HmBadge("● Active", bg = TealLight, textColor = TealMid)

@Composable
private fun GrantedBadge() = HmBadge("Granted", bg = GreenLight, textColor = GreenMid)

@Composable
private fun PartialBadge() = HmBadge("Partial", bg = AmberLight, textColor = AmberDark)

@Composable
private fun UnavailableBadge() = HmBadge(
    text = "Unavailable",
    bg = GrayLight,
    textColor = GrayMid,
    border = true,
)

@Composable
private fun HmBadge(
    text: String,
    bg: Color,
    textColor: Color,
    border: Boolean = false,
) {
    val modifier = Modifier
        .clip(RoundedCornerShape(99.dp))
        .background(bg)
        .then(
            if (border) Modifier.border(0.5.dp, CardBorder, RoundedCornerShape(99.dp))
            else Modifier
        )
        .padding(horizontal = 10.dp, vertical = 3.dp)

    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor, modifier = modifier)
}

// ─── Pills ────────────────────────────────────────────────────────────────────

@Composable
private fun GrantedPill() = HmPill("Granted", bg = GreenLight, textColor = GreenMid)

@Composable
private fun NotGrantedPill() = HmPill("Not granted", bg = AmberLight, textColor = AmberDark)

@Composable
private fun ActiveSmallPill() = HmPill("Active", bg = GreenLight, textColor = GreenMid)

@Composable
private fun HmPill(text: String, bg: Color, textColor: Color) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

// ─── Preview ─────────────────────────────────────────────────────────────────
//
// @Preview(showBackground = true, backgroundColor = 0xFFF5F5F0)
// @Composable
// fun PreviewUnavailable() {
//     MaterialTheme { Box(Modifier.padding(16.dp)) { HealthManagerCard(HomeUiState.HealthNotAvailable) } }
// }
//
// @Preview(showBackground = true, backgroundColor = 0xFFF5F5F0)
// @Composable
// fun PreviewNoPermission() {
//     MaterialTheme { Box(Modifier.padding(16.dp)) { HealthManagerCard(HomeUiState.Ready(true, false, null,
//         AuthStatus.NotAuthorized, AuthStatus.NotAuthorized)) } }
// }
//
// @Preview(showBackground = true, backgroundColor = 0xFFF5F5F0)
// @Composable
// fun PreviewPartial() {
//     MaterialTheme { Box(Modifier.padding(16.dp)) { HealthManagerCard(HomeUiState.Ready(true, false, null,
//         AuthStatus.Authorized, AuthStatus.NotAuthorized)) } }
// }
//
// @Preview(showBackground = true, backgroundColor = 0xFFF5F5F0)
// @Composable
// fun PreviewFullyGranted() {
//     MaterialTheme { Box(Modifier.padding(16.dp)) { HealthManagerCard(HomeUiState.Ready(true, false, null,
//         AuthStatus.Authorized, AuthStatus.Authorized)) } }
// }

