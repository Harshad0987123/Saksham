package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SakshamDestinations
import com.example.ui.theme.EmergencyContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.AppRole

sealed class NavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : NavItem(
        route = SakshamDestinations.HOME,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_item_home"
    )

    object Requests : NavItem(
        route = SakshamDestinations.REQUESTS,
        title = "Requests",
        selectedIcon = Icons.AutoMirrored.Filled.Assignment,
        unselectedIcon = Icons.AutoMirrored.Outlined.Assignment,
        testTag = "nav_item_requests"
    )

    object Transport : NavItem(
        route = SakshamDestinations.TRANSPORT_TAB,
        title = "Transport",
        selectedIcon = Icons.Filled.DirectionsCar,
        unselectedIcon = Icons.Outlined.DirectionsCar,
        testTag = "nav_item_transport"
    )

    object More : NavItem(
        route = SakshamDestinations.MORE,
        title = "More",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        testTag = "nav_item_more"
    )
}

/**
 * Responsive left-side navigation rail / sidebar.
 * Adapts between compact rail mode (on compact phones) and expanded drawer mode,
 * with an interactive expand/collapse toggle, active indicators, and role switcher.
 */
@Composable
fun SakshamSideNavigation(
    currentRoute: String?,
    currentRole: AppRole,
    sessionId: String,
    onRoleSelected: (AppRole) -> Unit,
    onQuickExit: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val isWideScreen = maxWidth >= 600.dp
        var userExpanded by remember { mutableStateOf(false) }
        val isExpanded = isWideScreen || userExpanded

        val railWidth by animateDpAsState(
            targetValue = if (isExpanded) 200.dp else 72.dp,
            label = "rail_width_anim"
        )

        var showRoleMenu by remember { mutableStateOf(false) }

        val items = listOf(
            NavItem.Home,
            NavItem.Requests,
            NavItem.Transport,
            NavItem.More
        )

        Surface(
            modifier = Modifier
                .width(railWidth)
                .fillMaxHeight()
                .testTag("left_side_navigation"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 12.dp, horizontal = if (isExpanded) 10.dp else 6.dp),
                horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally
            ) {
                // Header: Logo & Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TealPrimary)
                                .clickable {
                                    if (!isWideScreen) userExpanded = !userExpanded
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Saksham Safe",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (isExpanded) {
                            Column {
                                Text(
                                    text = "SAKSHAM",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Session: $sessionId",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (isExpanded && !isWideScreen) {
                        IconButton(
                            onClick = { userExpanded = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = "Collapse navigation",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Items
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally
                ) {
                    items.forEach { item ->
                        val isSelected = when (item.route) {
                            SakshamDestinations.HOME -> currentRoute == SakshamDestinations.HOME
                            SakshamDestinations.REQUESTS -> currentRoute == SakshamDestinations.REQUESTS || currentRoute == SakshamDestinations.REQUEST_STATUS
                            SakshamDestinations.TRANSPORT_TAB -> currentRoute == SakshamDestinations.TRANSPORT_TAB || currentRoute == SakshamDestinations.TRANSPORT_STATUS || currentRoute == SakshamDestinations.TRANSPORT_REQUEST
                            SakshamDestinations.MORE -> currentRoute in listOf(
                                SakshamDestinations.MORE,
                                SakshamDestinations.ACCOUNT,
                                SakshamDestinations.NOTIFICATIONS,
                                SakshamDestinations.PRIVACY,
                                SakshamDestinations.HELP_SUPPORT,
                                SakshamDestinations.SETTINGS,
                                SakshamDestinations.ABOUT
                            )
                            else -> currentRoute == item.route
                        }

                        Surface(
                            onClick = {
                                if (currentRoute != item.route) {
                                    onNavigateToRoute(item.route)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TealPrimary.copy(alpha = 0.14f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(item.testTag)
                        ) {
                            if (isExpanded) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Utilities: Role Switcher & Quick Exit
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isExpanded) Alignment.Start else Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Role Switcher Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { showRoleMenu = true },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("role_switcher_side_btn")
                        ) {
                            if (isExpanded) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (currentRole) {
                                                AppRole.SURVIVOR -> Icons.Default.Person
                                                AppRole.SHELTER_STAFF -> Icons.Default.NightShelter
                                                AppRole.TRANSPORT_PROVIDER -> Icons.Default.DirectionsCar
                                                AppRole.ADMIN -> Icons.Default.Shield
                                            },
                                            contentDescription = "Role",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = when (currentRole) {
                                                AppRole.SURVIVOR -> "Survivor"
                                                AppRole.SHELTER_STAFF -> "Shelter"
                                                AppRole.TRANSPORT_PROVIDER -> "Transport"
                                                AppRole.ADMIN -> "Admin"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (currentRole) {
                                            AppRole.SURVIVOR -> Icons.Default.Person
                                            AppRole.SHELTER_STAFF -> Icons.Default.NightShelter
                                            AppRole.TRANSPORT_PROVIDER -> Icons.Default.DirectionsCar
                                            AppRole.ADMIN -> Icons.Default.Shield
                                        },
                                        contentDescription = "Switch Role",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            AppRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = when (role) {
                                                    AppRole.SURVIVOR -> Icons.Default.Person
                                                    AppRole.SHELTER_STAFF -> Icons.Default.NightShelter
                                                    AppRole.TRANSPORT_PROVIDER -> Icons.Default.DirectionsCar
                                                    AppRole.ADMIN -> Icons.Default.Shield
                                                },
                                                contentDescription = null,
                                                tint = if (role == currentRole) TealPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = when (role) {
                                                    AppRole.SURVIVOR -> "Survivor View"
                                                    AppRole.SHELTER_STAFF -> "Shelter Staff"
                                                    AppRole.TRANSPORT_PROVIDER -> "Transport Driver"
                                                    AppRole.ADMIN -> "Admin Dashboard"
                                                },
                                                fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                                color = if (role == currentRole) TealPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    },
                                    onClick = {
                                        onRoleSelected(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick Exit Safety Action
                    Surface(
                        onClick = onQuickExit,
                        color = EmergencyContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_exit_side_btn")
                    ) {
                        if (isExpanded) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quick Exit",
                                    tint = EmergencyRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Quick Exit",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmergencyRed
                                    )
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quick Exit",
                                    tint = EmergencyRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
