package com.example.ui.screens.transport

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import com.example.ui.components.RequirementBadge
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@Composable
fun TransportDashboardScreen(
    transportRequests: List<TransportRequest>,
    transportProviders: List<TransportProvider>,
    onAssignVehicle: (Long, Long, TransportProvider, Int) -> Unit,
    onUpdateTransportStatus: (Long, Long, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarningAmber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Transport Provider Dispatch",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Dispatch accessible vehicles & track passenger safety",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section Title: Active Transport Requests
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DISPATCH QUEUE (${transportRequests.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (transportRequests.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No active transport requests in queue. In the Survivor flow, confirm a shelter placement and tap 'Request Transport' to dispatch a vehicle.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(transportRequests, key = { it.id }) { req ->
                TransportDispatchCard(
                    request = req,
                    providers = transportProviders,
                    onAssign = { provider, eta ->
                        onAssignVehicle(req.id, req.placementRequestId, provider, eta)
                    },
                    onAdvanceStatus = { nextStatus ->
                        onUpdateTransportStatus(req.id, req.placementRequestId, nextStatus)
                    }
                )
            }
        }

        // Registered Fleet Reference List (PRD Section 17)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REGISTERED FLEET PROVIDERS (${transportProviders.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(transportProviders, key = { it.id }) { provider ->
            FleetProviderCard(provider = provider)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransportDispatchCard(
    request: TransportRequest,
    providers: List<TransportProvider>,
    onAssign: (TransportProvider, Int) -> Unit,
    onAdvanceStatus: (String) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    // Matching logic (Section 8 & 9):
    // vehicle.passenger_capacity >= total_people
    // AND (if wheelchair_required: vehicle.wheelchair_accessible == true)
    // AND vehicle.available == true
    val suitableVehicles = providers.filter { p ->
        p.passengerCapacity >= request.passengers &&
        (!request.wheelchairRequired || p.wheelchairAccessible) &&
        p.available
    }

    var selectedProvider by remember(suitableVehicles) {
        mutableStateOf(suitableVehicles.firstOrNull())
    }
    var etaMinutes by remember(selectedProvider) {
        mutableIntStateOf(selectedProvider?.etaMinutes ?: (if (request.wheelchairRequired) 15 else 8))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transport_dispatch_card_${request.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transport Request #${request.id}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    color = when (request.status) {
                        "COMPLETED" -> FreshGreenContainer
                        "ON_THE_WAY" -> WarningAmberContainer
                        "ASSIGNED" -> TealContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (request.status) {
                            "ASSIGNED" -> "Vehicle Assigned"
                            "ON_THE_WAY" -> "On The Way"
                            "COMPLETED" -> "Arrived"
                            else -> "Requested"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (request.status) {
                                "COMPLETED" -> FreshGreen
                                "ON_THE_WAY" -> WarningAmber
                                "ASSIGNED" -> TealPrimary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Passenger Group Info (Section 10: Transport dashboard MUST clearly display passengers)
            Surface(
                color = TealContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Passengers: ${request.passengers}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    )
                    Text(
                        text = "Adults: ${request.adults} • Children: ${request.children}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Wheelchair Requirement
            Surface(
                color = if (request.wheelchairRequired) WarningAmberContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Accessible,
                        contentDescription = null,
                        tint = if (request.wheelchairRequired) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Wheelchair accessible: " + if (request.wheelchairRequired) "Required" else "Not Required",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (request.wheelchairRequired) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pickup: ${request.pickupArea} → Destination: ${request.destinationShelterName}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Current assigned details (Section 11)
            if (request.status in listOf("ASSIGNED", "ON_THE_WAY", "COMPLETED")) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Transport Assigned",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vehicle: ${request.vehicleType ?: "Accessible Vehicle"}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Capacity: ${request.vehicleCapacity ?: 7} passengers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Passengers: ${request.passengers}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Wheelchair accessible: ${if (request.wheelchairRequired) "Yes" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ETA: ${request.eta} minutes",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = TealPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Status Actions based on state
            when (request.status) {
                "REQUESTED" -> {
                    // Vehicle Assignment Section (Section 9: Automatically filter vehicles)
                    Text(
                        text = "Suitable vehicles for group (${request.passengers} passengers):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (suitableVehicles.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No suitable transport is currently available for your group.",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = expandedDropdown,
                            onExpandedChange = { expandedDropdown = !expandedDropdown },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedProvider?.let { "${it.vehicleType} — ${it.passengerCapacity} seats (${if (it.wheelchairAccessible) "Wheelchair accessible" else "Standard"})" } ?: "Select suitable vehicle",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Available Suitable Vehicle") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false }
                            ) {
                                suitableVehicles.forEach { p ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("${p.vehicleType} — ${p.passengerCapacity} seats", fontWeight = FontWeight.Bold)
                                                Text(
                                                    "${if (p.wheelchairAccessible) "Wheelchair accessible" else "Standard"} • ${p.driverName} (${p.vehiclePlate})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedProvider = p
                                            etaMinutes = p.etaMinutes
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                selectedProvider?.let { onAssign(it, etaMinutes) }
                            },
                            enabled = selectedProvider != null,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("assign_vehicle_btn_${request.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Assign Vehicle", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                "ASSIGNED" -> {
                    Button(
                        onClick = { onAdvanceStatus("ON_THE_WAY") },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("mark_on_the_way_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Driver On The Way", fontWeight = FontWeight.Bold)
                    }
                }

                "ON_THE_WAY" -> {
                    Button(
                        onClick = { onAdvanceStatus("COMPLETED") },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("mark_completed_btn_${request.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Arrival at Shelter", fontWeight = FontWeight.Bold)
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
                                text = "Arrived • Safe Arrival Confirmed",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreen
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FleetProviderCard(provider: TransportProvider) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
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
                        imageVector = if (provider.wheelchairAccessible) Icons.Default.Accessible else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = if (provider.wheelchairAccessible) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${provider.name} • ${provider.passengerCapacity} seats",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "${provider.vehicleType} • ${provider.driverName} (${provider.vehiclePlate})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = FreshGreenContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "AVAILABLE",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

