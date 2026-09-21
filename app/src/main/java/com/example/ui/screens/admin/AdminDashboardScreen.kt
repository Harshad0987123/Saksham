package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Shelter
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdminTab {
    DASHBOARD,
    PENDING,
    APPROVED,
    REJECTED,
    MORE
}

@Composable
fun AdminDashboardScreen(
    shelters: List<Shelter>,
    onReviewShelter: (Shelter) -> Unit,
    onLogout: () -> Unit,
    onSwitchRoleToSurvivor: () -> Unit
) {
    var currentTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    val pendingShelters = shelters.filter { it.verificationStatus == "pending" }
    val approvedShelters = shelters.filter { it.verificationStatus == "approved" }
    val rejectedShelters = shelters.filter { it.verificationStatus == "rejected" }

    val totalCount = shelters.size
    val pendingCount = pendingShelters.size
    val approvedCount = approvedShelters.size
    val rejectedCount = rejectedShelters.size

    Scaffold(
        bottomBar = {
            AdminBottomNavigation(
                currentTab = currentTab,
                onSelectTab = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Admin Top Header
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NavySecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "SAKSHAM ADMIN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = NavySecondary
                            )
                            Text(
                                text = "Shelter Accreditation Authority",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = onSwitchRoleToSurvivor,
                            color = TealContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Survivor View",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = TealPrimary
                                )
                            }
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            when (currentTab) {
                AdminTab.DASHBOARD -> AdminDashboardOverview(
                    totalCount = totalCount,
                    pendingCount = pendingCount,
                    approvedCount = approvedCount,
                    rejectedCount = rejectedCount,
                    pendingShelters = pendingShelters,
                    onReviewShelter = onReviewShelter,
                    onViewAllPending = { currentTab = AdminTab.PENDING }
                )
                AdminTab.PENDING -> ShelterListView(
                    title = "Pending Approvals",
                    subtitle = "Shelters awaiting admin verification before being listed",
                    shelters = pendingShelters,
                    emptyText = "No pending shelters awaiting verification.",
                    onReviewShelter = onReviewShelter
                )
                AdminTab.APPROVED -> ShelterListView(
                    title = "Approved Shelters",
                    subtitle = "Verified shelters visible to survivors in need",
                    shelters = approvedShelters,
                    emptyText = "No approved shelters currently on file.",
                    onReviewShelter = onReviewShelter
                )
                AdminTab.REJECTED -> ShelterListView(
                    title = "Rejected Shelters",
                    subtitle = "Shelters filtered out from survivor searches",
                    shelters = rejectedShelters,
                    emptyText = "No rejected shelters currently on file.",
                    onReviewShelter = onReviewShelter
                )
                AdminTab.MORE -> AdminSettingsView(
                    onLogout = onLogout,
                    onSwitchToSurvivor = onSwitchRoleToSurvivor
                )
            }
        }
    }
}
@Composable
private fun AdminDashboardOverview(
    totalCount: Int,
    pendingCount: Int,
    approvedCount: Int,
    rejectedCount: Int,
    pendingShelters: List<Shelter>,
    onReviewShelter: (Shelter) -> Unit,
    onViewAllPending: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ADMIN DASHBOARD",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage shelter verifications and survivor search visibility",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(title = "Total Shelters", count = "$totalCount", color = NavySecondary, modifier = Modifier.weight(1f))
                    AdminStatCard(title = "Pending Approval", count = "$pendingCount", color = WarningAmber, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(title = "Approved", count = "$approvedCount", color = FreshGreen, modifier = Modifier.weight(1f))
                    AdminStatCard(title = "Rejected", count = "$rejectedCount", color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pending Shelter Approvals (${pendingShelters.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (pendingShelters.isNotEmpty()) {
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TealPrimary),
                        modifier = Modifier.clickable(onClick = onViewAllPending).padding(start = 12.dp)
                    )
                }
            }
        }

        if (pendingShelters.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(32.dp))
                        Text(text = "All Shelters Reviewed", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "There are no pending shelter verification requests at this moment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingShelters, key = { it.id }) { shelter ->
                AdminShelterCard(shelter = shelter, onReview = { onReviewShelter(shelter) })
            }
        }
    }
}
@Composable
private fun ShelterListView(
    title: String,
    subtitle: String,
    shelters: List<Shelter>,
    emptyText: String,
    onReviewShelter: (Shelter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (shelters.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = emptyText, modifier = Modifier.padding(20.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(shelters, key = { it.id }) { shelter ->
                AdminShelterCard(shelter = shelter, onReview = { onReviewShelter(shelter) })
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = count, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = color))
        }
    }
}
@Composable
fun AdminShelterCard(
    shelter: Shelter,
    onReview: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(if (shelter.createdAt > 0) shelter.createdAt else shelter.lastUpdated))
    val isToday = (System.currentTimeMillis() - shelter.createdAt) < (24 * 60 * 60 * 1000)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_shelter_card_${shelter.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Name + badge row - badge never overlaps name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = shelter.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Surface(
                    color = when (shelter.verificationStatus) {
                        "approved" -> FreshGreenContainer
                        "rejected" -> MaterialTheme.colorScheme.errorContainer
                        else -> WarningAmberContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (shelter.verificationStatus) {
                            "approved" -> "Approved"
                            "rejected" -> "Rejected"
                            else -> "Pending"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (shelter.verificationStatus) {
                                "approved" -> FreshGreen
                                "rejected" -> MaterialTheme.colorScheme.error
                                else -> WarningAmber
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = NavySecondary, modifier = Modifier.size(15.dp))
                Text(text = shelter.area, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "Capacity: ${shelter.totalBeds}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "${shelter.availableBeds} beds available", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = "Submitted: ${if (isToday) "Today" else formattedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (shelter.verificationStatus == "rejected" && !shelter.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Reason: ${shelter.rejectionReason}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onReview,
                colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth().testTag("review_shelter_btn_${shelter.id}")
            ) {
                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Review", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
@Composable
private fun AdminBottomNavigation(
    currentTab: AdminTab,
    onSelectTab: (AdminTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = currentTab == AdminTab.DASHBOARD,
            onClick = { onSelectTab(AdminTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavySecondary, indicatorColor = TealContainer)
        )
        NavigationBarItem(
            selected = currentTab == AdminTab.PENDING,
            onClick = { onSelectTab(AdminTab.PENDING) },
            icon = { Icon(Icons.Default.HourglassTop, contentDescription = "Approvals") },
            label = { Text("Approvals") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavySecondary, indicatorColor = TealContainer)
        )
        NavigationBarItem(
            selected = currentTab == AdminTab.APPROVED,
            onClick = { onSelectTab(AdminTab.APPROVED) },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Approved") },
            label = { Text("Approved") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavySecondary, indicatorColor = TealContainer)
        )
        NavigationBarItem(
            selected = currentTab == AdminTab.REJECTED,
            onClick = { onSelectTab(AdminTab.REJECTED) },
            icon = { Icon(Icons.Default.Close, contentDescription = "Rejected") },
            label = { Text("Rejected") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavySecondary, indicatorColor = TealContainer)
        )
        NavigationBarItem(
            selected = currentTab == AdminTab.MORE,
            onClick = { onSelectTab(AdminTab.MORE) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "More") },
            label = { Text("More") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = NavySecondary, indicatorColor = TealContainer)
        )
    }
}

@Composable
private fun AdminSettingsView(
    onLogout: () -> Unit,
    onSwitchToSurvivor: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "ADMIN SETTINGS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Active Account: Saksham Administrator", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "admin@saksham.gov.in • Verification & Safety Board", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = onSwitchToSurvivor, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch to Survivor Safety Search")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onLogout, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out of Admin Portal")
                    }
                }
            }
        }
    }
}