package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.SakshamDestinations
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TealPrimary

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(
        route = SakshamDestinations.HOME,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_item_home"
    )

    object Requests : BottomNavItem(
        route = SakshamDestinations.REQUESTS,
        title = "Requests",
        selectedIcon = Icons.Filled.Assignment,
        unselectedIcon = Icons.Outlined.Assignment,
        testTag = "nav_item_requests"
    )

    object Transport : BottomNavItem(
        route = SakshamDestinations.TRANSPORT_TAB,
        title = "Transport",
        selectedIcon = Icons.Filled.DirectionsCar,
        unselectedIcon = Icons.Outlined.DirectionsCar,
        testTag = "nav_item_transport"
    )

    object More : BottomNavItem(
        route = SakshamDestinations.MORE,
        title = "More",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        testTag = "nav_item_more"
    )
}

@Composable
fun SakshamBottomNavigation(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Requests,
        BottomNavItem.Transport,
        BottomNavItem.More
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
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

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigateToRoute(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = TealPrimary.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
