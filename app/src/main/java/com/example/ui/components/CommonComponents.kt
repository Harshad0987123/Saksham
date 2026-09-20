package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FreshnessLevel
import com.example.data.model.calculateFreshness
import com.example.ui.theme.EmergencyContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OnEmergencyContainer
import com.example.ui.theme.OnTealContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.viewmodel.AppRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SakshamTopAppBar(
    currentRole: AppRole,
    sessionId: String,
    onRoleSelected: (AppRole) -> Unit,
    onQuickExit: () -> Unit,
    onEmergencyClick: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Saksham Safe",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SAKSHAM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Session: $sessionId",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Role Switcher button (for judges / seamless workflow demo)
                    Box {
                        Surface(
                            onClick = { showRoleMenu = true },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("role_switcher_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = when (currentRole) {
                                        AppRole.SURVIVOR -> Icons.Default.Person
                                        AppRole.SHELTER_STAFF -> Icons.Default.NightShelter
                                        AppRole.TRANSPORT_PROVIDER -> Icons.Default.DirectionsCar
                                    },
                                    contentDescription = "Switch Role",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = when (currentRole) {
                                        AppRole.SURVIVOR -> "Survivor"
                                        AppRole.SHELTER_STAFF -> "Shelter"
                                        AppRole.TRANSPORT_PROVIDER -> "Transport"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            "Survivor Flow",
                                            fontWeight = if (currentRole == AppRole.SURVIVOR) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text("Find shelter & request placement", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary)
                                },
                                onClick = {
                                    onRoleSelected(AppRole.SURVIVOR)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            "Shelter Dashboard",
                                            fontWeight = if (currentRole == AppRole.SHELTER_STAFF) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text("Manage beds & confirm requests", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.NightShelter, contentDescription = null, tint = NavySecondary)
                                },
                                onClick = {
                                    onRoleSelected(AppRole.SHELTER_STAFF)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            "Transport Dashboard",
                                            fontWeight = if (currentRole == AppRole.TRANSPORT_PROVIDER) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text("Assign vehicles & coordinate travel", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = WarningAmber)
                                },
                                onClick = {
                                    onRoleSelected(AppRole.TRANSPORT_PROVIDER)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }

                    // Quick Exit / Emergency Clears
                    IconButton(
                        onClick = onQuickExit,
                        modifier = Modifier.testTag("quick_exit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quick Privacy Exit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun FreshnessBadge(lastUpdatedMs: Long) {
    val (level, text) = calculateFreshness(lastUpdatedMs)
    val (bgColor, textColor) = when (level) {
        FreshnessLevel.RECENTLY_CONFIRMED -> FreshGreenContainer to FreshGreen
        FreshnessLevel.UPDATED_RECENTLY -> WarningAmberContainer to WarningAmber
        FreshnessLevel.NEEDS_CONFIRMATION -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun PrivacyBanner(
    modifier: Modifier = Modifier,
    message: String = "Privacy Protected: Zero names, Aadhaar, or exact addresses collected."
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RequirementBadge(
    label: String,
    isAccepted: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isAccepted) FreshGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isAccepted) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isAccepted) FreshGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = if (isAccepted) FreshGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
