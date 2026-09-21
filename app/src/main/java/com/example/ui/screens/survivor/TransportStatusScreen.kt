package com.example.ui.screens.survivor

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportRequest
import com.example.ui.components.PrivacyBanner
import com.example.ui.theme.EmergencyContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.OnEmergencyContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@Composable
fun TransportStatusScreen(
    transportRequest: TransportRequest?,
    onSwitchToTransportDashboardClick: () -> Unit,
    onEmergencyCallClick: () -> Unit,
    onBackClick: () -> Unit
) {
    if (transportRequest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active transport request found.")
        }
        return
    }

    val scrollState = rememberScrollState()
    val isAssigned = transportRequest.status in listOf("ASSIGNED", "ON_THE_WAY", "ARRIVED", "COMPLETED")
    val isOnTheWay = transportRequest.status in listOf("ON_THE_WAY", "ARRIVED", "COMPLETED")
    val isCompleted = transportRequest.status in listOf("ARRIVED", "COMPLETED")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Navigation Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("transport_status_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Transport Coordination",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Transport #${transportRequest.id} • Placement #${transportRequest.placementRequestId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transport Hero Card (PRD FR-12)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isCompleted -> FreshGreenContainer
                    isOnTheWay -> WarningAmberContainer
                    isAssigned -> TealContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> FreshGreen
                                isOnTheWay -> WarningAmber
                                isAssigned -> TealPrimary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (transportRequest.status) {
                        "COMPLETED" -> "ARRIVED SAFELY AT SHELTER"
                        "ON_THE_WAY" -> "VEHICLE EN ROUTE"
                        "ASSIGNED" -> "VEHICLE ASSIGNED"
                        else -> "SEARCHING FOR VEHICLE"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = when {
                        isCompleted -> FreshGreen
                        isOnTheWay -> WarningAmber
                        else -> TealPrimary
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (transportRequest.status) {
                        "COMPLETED" -> "You have safely arrived at ${transportRequest.destinationShelterName}. Present your verification PIN at reception."
                        "ON_THE_WAY" -> "Driver ${transportRequest.driverName} is on the way. Estimated arrival: ~${transportRequest.eta} minutes."
                        "ASSIGNED" -> "A verified transport provider has been assigned. Getting ready for dispatch."
                        else -> "Dispatching request to nearest available accessible drivers..."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (isAssigned && !isCompleted) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Estimated Arrival Time",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${transportRequest.eta} mins",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = TealPrimary
                                )
                            }
                            Surface(
                                color = TealContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "LIVE DISPATCH",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TealPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Demo Helper Card for Judges
        if (!isCompleted) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hackathon Testing Shortcut",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "To assign a vehicle or advance transport status (On the way / Completed), switch to Transport Provider Dashboard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSwitchToTransportDashboardClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("switch_to_transport_dash_btn")
                    ) {
                        Text("Open Transport Provider Dashboard", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Assigned Vehicle Details (PRD FR-12)
        if (isAssigned) {
            Text(
                text = "ASSIGNED VEHICLE DETAILS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    VehicleInfoRow(
                        label = "Provider",
                        value = transportRequest.providerName?.ifEmpty { "City SafeRide Transit" } ?: "City SafeRide Transit"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VehicleInfoRow(
                        label = "Vehicle Type",
                        value = transportRequest.vehicleType?.ifEmpty { "Accessible Van" } ?: "Accessible Van"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VehicleInfoRow(
                        label = "Driver Name",
                        value = transportRequest.driverName?.ifEmpty { "Assigned Driver" } ?: "Assigned Driver"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VehicleInfoRow(
                        label = "License Plate",
                        value = transportRequest.vehiclePlate?.ifEmpty { "DL-04-AR-8821" } ?: "DL-04-AR-8821"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VehicleInfoRow(
                        label = "Destination",
                        value = transportRequest.destinationShelterName
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4-Stage Transport Stepper
        Text(
            text = "TRANSPORT TIMELINE",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        TransportStepRow(
            title = "Transport Requested",
            description = "Pickup from ${transportRequest.pickupArea}",
            isDone = true
        )
        TransportStepRow(
            title = "Vehicle & Driver Assigned",
            description = if (isAssigned) "${transportRequest.providerName} (${transportRequest.vehicleType})" else "Matching available vehicles...",
            isDone = isAssigned
        )
        TransportStepRow(
            title = "Driver On The Way",
            description = if (isOnTheWay) "Live transit to pickup zone" else "Pending dispatch",
            isDone = isOnTheWay
        )
        TransportStepRow(
            title = "Arrived at Safe Shelter",
            description = if (isCompleted) "Safely delivered to ${transportRequest.destinationShelterName}" else "In progress",
            isDone = isCompleted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // In-transit Safety Banner
        Surface(
            color = EmergencyContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Need Immediate Assistance?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnEmergencyContainer
                    )
                    Text(
                        text = "Direct 24/7 National Emergency connection",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnEmergencyContainer
                    )
                }
                Button(
                    onClick = onEmergencyCallClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("transport_emergency_call_btn")
                ) {
                    Icon(Icons.Default.Emergency, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("112", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun VehicleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TransportStepRow(
    title: String,
    description: String,
    isDone: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isDone) FreshGreen else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
