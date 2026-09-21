package com.example.ui.screens.transport

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import com.example.data.model.formatPassengerBreakdown
import com.example.data.model.formatTotalPeople
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults

@Composable
fun TransportDashboardScreen(
    transportRequests: List<TransportRequest>,
    transportProviders: List<TransportProvider>,
    onAssignVehicle: (Long, Long, TransportProvider, Int) -> Unit,
    onUpdateTransportStatus: (Long, Long, Long?, String) -> Unit,
    onSetEta: (Long, Int) -> Unit,
    onToggleVehicleAvailability: (Long, Boolean) -> Unit,
    onDeclineRequest: (Long, String) -> Unit = { _, _ -> },
    onSwitchRoleToSurvivor: () -> Unit = {}
) {
    BackHandler {
        onSwitchRoleToSurvivor()
    }

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedRequestForDetails by remember { mutableStateOf<TransportRequest?>(null) }
    var requestToDecline by remember { mutableStateOf<TransportRequest?>(null) }

    val activeRequestDetails = transportRequests.find { it.id == selectedRequestForDetails?.id } ?: selectedRequestForDetails

    // Live counts from actual database data
    val pendingCount = transportRequests.count { it.status == "REQUESTED" }
    val assignedCount = transportRequests.count { it.status == "ASSIGNED" }
    val onTheWayCount = transportRequests.count { it.status == "ON_THE_WAY" }
    val arrivedCompletedCount = transportRequests.count { it.status in listOf("ARRIVED", "COMPLETED") }
    val availableVehiclesCount = transportProviders.count { it.available }

    // Categorized requests for the prompt-specified sections
    val newRequests = transportRequests.filter { it.status == "REQUESTED" }
    val assignedRequests = transportRequests.filter { it.status == "ASSIGNED" }
    val onTheWayRequests = transportRequests.filter { it.status == "ON_THE_WAY" }
    val completedRequests = transportRequests.filter { it.status in listOf("ARRIVED", "COMPLETED") }
    val declinedRequests = transportRequests.filter { it.status == "DECLINED" }

    // Filter requests
    val filteredRequests = when (selectedFilter) {
        "New Requests" -> newRequests
        "Assigned" -> assignedRequests
        "On The Way" -> onTheWayRequests
        "Completed" -> completedRequests
        "Declined" -> declinedRequests
        else -> transportRequests
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onSwitchRoleToSurvivor,
                    modifier = Modifier.testTag("transport_dash_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Survivor Flow"
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavySecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Transport Operations",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Manage transport requests and vehicle assignments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Summary Cards (4 live database values)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Pending",
                    count = pendingCount,
                    containerColor = WarningAmberContainer,
                    contentColor = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Assigned",
                    count = assignedCount,
                    containerColor = TealContainer,
                    contentColor = TealPrimary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "On The Way",
                    count = onTheWayCount,
                    containerColor = TealContainer.copy(alpha = 0.5f),
                    contentColor = NavySecondary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Available",
                    count = availableVehiclesCount,
                    containerColor = FreshGreenContainer,
                    contentColor = FreshGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filters Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("All", "New Requests", "Assigned", "On The Way", "Completed", "Declined")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavySecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Section Title: ACTIVE TRANSPORT REQUESTS
        item {
            Text(
                text = "ACTIVE TRANSPORT REQUESTS (${filteredRequests.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (filteredRequests.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No transport requests found for '$selectedFilter'.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredRequests, key = { "request_${it.id}" }) { req ->
                TransportRequestCard(
                    request = req,
                    onViewRequest = { selectedRequestForDetails = req },
                    onDeclineRequest = { requestToDecline = req }
                )
            }
        }

        // Section Title: AVAILABLE VEHICLES
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE VEHICLES (${transportProviders.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$availableVehiclesCount Available",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen
                    )
                )
            }
        }

        items(transportProviders, key = { "provider_${it.id}" }) { provider ->
            VehicleFleetCard(
                provider = provider,
                onToggleAvailability = { onToggleVehicleAvailability(provider.id, !provider.available) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    // Transport Request Details Sheet / Dialog
    if (activeRequestDetails != null) {
        TransportRequestDetailsModal(
            request = activeRequestDetails,
            transportProviders = transportProviders,
            onDismiss = { selectedRequestForDetails = null },
            onAssignVehicle = { provider, eta ->
                onAssignVehicle(activeRequestDetails.id, activeRequestDetails.placementRequestId, provider, eta)
            },
            onUpdateStatus = { status ->
                onUpdateTransportStatus(
                    activeRequestDetails.id,
                    activeRequestDetails.placementRequestId,
                    activeRequestDetails.providerId,
                    status
                )
            },
            onSetEta = { eta ->
                onSetEta(activeRequestDetails.id, eta)
            },
            onDeclineClick = {
                requestToDecline = activeRequestDetails
            }
        )
    }

    // Transport Decline Reason Dialog
    if (requestToDecline != null) {
        val targetToDecline = requestToDecline!!
        TransportDeclineDialog(
            request = targetToDecline,
            onDismiss = { requestToDecline = null },
            onConfirmDecline = { reason ->
                onDeclineRequest(targetToDecline.id, reason)
                requestToDecline = null
                if (selectedRequestForDetails?.id == targetToDecline.id) {
                    selectedRequestForDetails = null
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TransportRequestCard(
    request: TransportRequest,
    onViewRequest: () -> Unit,
    onDeclineRequest: () -> Unit = {}
) {
    val statusLabel = when (request.status) {
        "ASSIGNED" -> "ASSIGNED"
        "ON_THE_WAY" -> "ON THE WAY"
        "ARRIVED" -> "ARRIVED"
        "COMPLETED" -> "COMPLETED"
        "DECLINED" -> "DECLINED"
        else -> "REQUESTED"
    }

    val statusColor = when (request.status) {
        "ASSIGNED" -> TealPrimary
        "ON_THE_WAY" -> WarningAmber
        "ARRIVED", "COMPLETED" -> FreshGreen
        "DECLINED" -> MaterialTheme.colorScheme.error
        else -> WarningAmber
    }

    val statusContainerColor = when (request.status) {
        "ASSIGNED" -> TealContainer
        "ON_THE_WAY" -> WarningAmberContainer
        "ARRIVED", "COMPLETED" -> FreshGreenContainer
        "DECLINED" -> MaterialTheme.colorScheme.errorContainer
        else -> WarningAmberContainer.copy(alpha = 0.5f)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transport_request_card_${request.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transport Request #${request.id}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = statusContainerColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Passengers breakdown using exact grammar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Passengers:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatPassengerBreakdown(request.adults, request.children),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total People:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${request.passengers}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Wheelchair:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (request.wheelchairRequired) "Required" else "Not Required",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (request.wheelchairRequired) WarningAmber else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Destination:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = request.destinationShelterName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Area: ${request.pickupArea}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.status == "DECLINED" && !request.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Declined: ${request.rejectionReason}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (request.status == "REQUESTED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("view_request_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Review & Assign", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDeclineRequest,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.65f)
                            .height(44.dp)
                            .testTag("decline_request_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = onViewRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("view_request_btn_${request.id}")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Details & Status", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TransportRequestDetailsModal(
    request: TransportRequest,
    transportProviders: List<TransportProvider>,
    onDismiss: () -> Unit,
    onAssignVehicle: (TransportProvider, Int) -> Unit,
    onUpdateStatus: (String) -> Unit,
    onSetEta: (Int) -> Unit,
    onDeclineClick: () -> Unit = {}
) {
    var vehicleToConfirm by remember { mutableStateOf<TransportProvider?>(null) }
    var selectedEta by remember { mutableIntStateOf(request.eta) }

    // Real Vehicle Suitability Matching
    // Vehicle suitable ONLY when:
    // available == true
    // AND capacity >= totalPeople (request.passengers)
    // AND (if wheelchairRequired == true: wheelchairAccessible == true)
    // Prefer displaying vehicles with the smallest sufficient capacity first
    val suitableVehicles = transportProviders.filter { p ->
        p.available &&
        p.passengerCapacity >= request.passengers &&
        (!request.wheelchairRequired || p.wheelchairAccessible)
    }.sortedBy { it.passengerCapacity }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Transport Request #${request.id}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PASSENGERS
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "PASSENGERS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatPassengerBreakdown(request.adults, request.children),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Total: ${formatTotalPeople(request.passengers)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ACCESSIBILITY
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ACCESSIBILITY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Wheelchair Required: ${if (request.wheelchairRequired) "Yes" else "No"}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (request.wheelchairRequired) WarningAmber else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                // DESTINATION
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "DESTINATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = request.destinationShelterName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Area: ${request.pickupArea}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // STATUS & VEHICLE
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "STATUS & ASSIGNED VEHICLE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status: ${request.status.replace('_', ' ')}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Vehicle: ${request.vehicleType ?: request.providerName ?: "Not Assigned"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (request.status in listOf("ASSIGNED", "ON_THE_WAY")) {
                            Text(
                                text = "ETA: ${request.eta} minutes",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TealPrimary
                            )
                        }
                    }
                }

                HorizontalDivider()

                // SUITABLE AVAILABLE VEHICLES
                Text(
                    text = "Suitable Available Vehicles",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                if (suitableVehicles.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "No suitable vehicles are currently available.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Required capacity: ${request.passengers} passengers${if (request.wheelchairRequired) " + Wheelchair Accessible" else ""}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                } else {
                    suitableVehicles.forEach { vehicle ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = vehicle.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Type: ${vehicle.vehicleType} • Capacity: ${vehicle.passengerCapacity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Wheelchair Accessible: ${if (vehicle.wheelchairAccessible) "Yes" else "No"} • Available: Yes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (request.status == "REQUESTED") {
                                    Button(
                                        onClick = { vehicleToConfirm = vehicle },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Assign", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // If in REQUESTED state, provide explicit Decline action
                if (request.status == "REQUESTED") {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onDeclineClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Decline Transport Request", fontWeight = FontWeight.SemiBold)
                    }
                }

                // ETA Selection (if assigned)
                if (request.status in listOf("ASSIGNED", "ON_THE_WAY")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Update Estimated Arrival (ETA):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(10, 15, 20, 30, 45, 60).forEach { etaOption ->
                            OutlinedButton(
                                onClick = {
                                    selectedEta = etaOption
                                    onSetEta(etaOption)
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = if (request.eta == etaOption) ButtonDefaults.outlinedButtonColors(
                                    containerColor = TealContainer
                                ) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("$etaOption min")
                            }
                        }
                    }
                }

                // Status Actions
                Spacer(modifier = Modifier.height(8.dp))
                when (request.status) {
                    "ASSIGNED" -> {
                        Button(
                            onClick = { onUpdateStatus("ON_THE_WAY") },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Trip (On The Way)", fontWeight = FontWeight.Bold)
                        }
                    }
                    "ON_THE_WAY" -> {
                        Button(
                            onClick = { onUpdateStatus("ARRIVED") },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Arrived at Shelter", fontWeight = FontWeight.Bold)
                        }
                    }
                    "ARRIVED" -> {
                        Button(
                            onClick = { onUpdateStatus("COMPLETED") },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete Trip (Release Vehicle)", fontWeight = FontWeight.Bold)
                        }
                    }
                    "COMPLETED" -> {
                        Surface(
                            color = FreshGreenContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreshGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "✓ Transport Completed & Vehicle Available",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FreshGreen
                                    )
                                )
                            }
                        }
                    }
                    "DECLINED" -> {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "✕ Transport Request Declined",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                                if (!request.rejectionReason.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Reason: ${request.rejectionReason}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    // Confirmation Dialog for Assigning Vehicle
    if (vehicleToConfirm != null) {
        val targetVehicle = vehicleToConfirm!!
        AlertDialog(
            onDismissRequest = { vehicleToConfirm = null },
            title = {
                Text(
                    text = "Assign Vehicle?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Vehicle: ${targetVehicle.name}")
                    Text("Capacity: ${targetVehicle.passengerCapacity}")
                    Text("Wheelchair Accessible: ${if (targetVehicle.wheelchairAccessible) "Yes" else "No"}")
                    Text("Passengers: ${request.passengers}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAssignVehicle(targetVehicle, targetVehicle.etaMinutes)
                        vehicleToConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Confirm Assignment")
                }
            },
            dismissButton = {
                TextButton(onClick = { vehicleToConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TransportDeclineDialog(
    request: TransportRequest,
    onDismiss: () -> Unit,
    onConfirmDecline: (String) -> Unit
) {
    val predefinedReasons = listOf(
        "No suitable vehicle currently available.",
        "No suitable wheelchair-accessible vehicle currently available.",
        "Passenger capacity exceeds available fleet limits.",
        "Severe weather or route obstruction."
    )
    var selectedReason by remember { mutableStateOf(predefinedReasons[0]) }
    var customReason by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Decline Transport #${request.id}?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select a reason to inform survivor immediately:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                predefinedReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedReason = reason
                                isCustom = false
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCustom && selectedReason == reason,
                            onClick = {
                                selectedReason = reason
                                isCustom = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.error)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCustom = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCustom,
                        onClick = { isCustom = true },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.error)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Other reason...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isCustom) {
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        placeholder = { Text("Enter decline reason...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (isCustom && customReason.isNotBlank()) customReason.trim() else selectedReason
                    onConfirmDecline(finalReason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Confirm Decline")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun VehicleFleetCard(
    provider: TransportProvider,
    onToggleAvailability: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (provider.wheelchairAccessible) TealContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (provider.wheelchairAccessible) Icons.AutoMirrored.Filled.Accessible else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = if (provider.wheelchairAccessible) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${provider.name} • Capacity: ${provider.passengerCapacity}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Wheelchair Accessible: ${if (provider.wheelchairAccessible) "Yes" else "No"} • Driver: ${provider.driverName} (${provider.vehiclePlate})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                onClick = onToggleAvailability,
                color = if (provider.available) FreshGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (provider.available) "Available" else "Assigned / Off",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (provider.available) FreshGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
