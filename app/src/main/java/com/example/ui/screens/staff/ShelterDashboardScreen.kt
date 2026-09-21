package com.example.ui.screens.staff

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AvailabilityLog
import com.example.data.model.PlacementRequest
import com.example.data.model.Shelter
import com.example.ui.components.FreshnessBadge
import com.example.ui.components.RequirementBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterDashboardScreen(
    shelters: List<Shelter>,
    selectedShelterId: Long,
    placementRequests: List<PlacementRequest>,
    availabilityLogs: List<AvailabilityLog>,
    onSelectShelter: (Long) -> Unit,
    onUpdateBeds: (Long, Int, String) -> Unit,
    onConfirmRequest: (Long) -> Unit,
    onRejectRequest: (Long) -> Unit
) {
    val activeShelter = shelters.find { it.id == selectedShelterId } ?: shelters.firstOrNull()
    var bedInput by remember(activeShelter) { mutableIntStateOf(activeShelter?.availableBeds ?: 4) }
    var shelterDropdownExpanded by remember { mutableStateOf(false) }

    val currentShelterRequests = placementRequests.filter {
        activeShelter == null || it.shelterId == activeShelter.id
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NightShelter,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Shelter Staff Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Live bed management & rapid intake confirmation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Shelter Selector Dropdown (PRD FR-13)
        item {
            ExposedDropdownMenuBox(
                expanded = shelterDropdownExpanded,
                onExpandedChange = { shelterDropdownExpanded = !shelterDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = activeShelter?.let { "${it.name} (${it.area})" } ?: "Select Shelter",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Active Shelter Managed") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shelterDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                        .testTag("staff_shelter_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = shelterDropdownExpanded,
                    onDismissRequest = { shelterDropdownExpanded = false }
                ) {
                    shelters.forEach { s ->
                        DropdownMenuItem(
                            text = { Text("${s.name} (${s.area}) • ${s.availableBeds} beds") },
                            onClick = {
                                onSelectShelter(s.id)
                                bedInput = s.availableBeds
                                shelterDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Active Shelter Verification & Info Card (PRD Section 15)
        if (activeShelter != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = activeShelter.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            if (activeShelter.verified && activeShelter.verificationStatus == "approved") {
                                Surface(
                                    color = FreshGreenContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "✓ Verified by Saksham",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = FreshGreen
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Verification Status:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = activeShelter.verificationStatus.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeShelter.verificationStatus == "approved") FreshGreen else WarningAmber
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Available Spaces:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${activeShelter.availableBeds} beds",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Bed Adjustment Card (PRD FR-13, FR-14)
        if (activeShelter != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "AVAILABLE BEDS ADJUSTER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = NavySecondary
                            )
                            FreshnessBadge(lastUpdatedMs = activeShelter.lastUpdated)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stepper row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilledIconButton(
                                onClick = { if (bedInput > 0) bedInput-- },
                                icon = Icons.Default.Remove,
                                contentDescription = "Decrease Beds"
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$bedInput",
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "beds of ${activeShelter.totalBeds} total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            FilledIconButton(
                                onClick = { bedInput++ },
                                icon = Icons.Default.Add,
                                contentDescription = "Increase Beds"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick presets row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0, 1, 2, 4, 6, 8).forEach { preset ->
                                OutlinedButton(
                                    onClick = { bedInput = preset },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = if (bedInput == preset) ButtonDefaults.outlinedButtonColors(
                                        containerColor = TealContainer
                                    ) else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text(
                                        text = "$preset",
                                        fontWeight = if (bedInput == preset) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onUpdateBeds(activeShelter.id, bedInput, activeShelter.name)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("update_beds_btn")
                        ) {
                            Icon(Icons.Default.Update, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish Bed Count (${bedInput} Available)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Incoming Intake Queue (PRD FR-15, FR-16)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "INCOMING PLACEMENT REQUESTS (${currentShelterRequests.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (currentShelterRequests.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No pending placement requests for this shelter right now. Use the Survivor flow to submit an intake request!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(currentShelterRequests, key = { "request_${it.id}" }) { req ->
                StaffRequestCard(
                    request = req,
                    onConfirm = { onConfirmRequest(req.id) },
                    onReject = { onRejectRequest(req.id) }
                )
            }
        }

        // Availability Audit Trail (PRD FR-14)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(
                    text = "AVAILABILITY UPDATE LOG (AUDIT)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        val recentLogs = availabilityLogs.take(5)
        if (recentLogs.isEmpty()) {
            item {
                Text(
                    text = "No logged updates yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recentLogs, key = { "log_${it.id}" }) { log ->
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val timeString = timeFormat.format(Date(log.updatedAt))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = log.shelterName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Beds updated to: ${log.availableBeds}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = TealPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun FilledIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = TealContainer,
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = TealPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StaffRequestCard(
    request: PlacementRequest,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    val isPending = request.status in listOf("REQUEST_SENT", "PENDING")
    val isConfirmed = request.status in listOf("CONFIRMED", "TRANSPORT_REQUESTED", "TRANSPORT_ASSIGNED", "ON_THE_WAY", "COMPLETED")

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("staff_request_card_${request.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ticket #${request.id} • Session ${request.sessionId}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    color = when {
                        isConfirmed -> FreshGreenContainer
                        request.status == "REJECTED" -> MaterialTheme.colorScheme.errorContainer
                        else -> TealContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = request.status.replace('_', ' '),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = when {
                                isConfirmed -> FreshGreen
                                request.status == "REJECTED" -> MaterialTheme.colorScheme.error
                                else -> TealPrimary
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Accommodations tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RequirementBadge(
                    label = if (request.needsChildren) "Child Accompaniment" else "Single Individual",
                    isAccepted = request.needsChildren,
                    modifier = Modifier.weight(1f)
                )
                RequirementBadge(
                    label = if (request.needsWheelchair) "Wheelchair Accessible" else "Standard Entry",
                    isAccepted = request.needsWheelchair,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Service: ${request.serviceType} • Shelter: ${request.shelterName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isConfirmed && request.confirmationCode.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = FreshGreenContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Intake PIN Generated: ${request.confirmationCode}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FreshGreen
                        )
                    )
                }
            }

            // Action Buttons
            if (isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("staff_reject_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline")
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("staff_confirm_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Space")
                    }
                }
            }
        }
    }
}
