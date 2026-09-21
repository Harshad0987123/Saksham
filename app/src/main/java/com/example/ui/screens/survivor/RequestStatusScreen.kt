package com.example.ui.screens.survivor

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlacementRequest
import com.example.data.model.TransportRequest
import com.example.data.model.formatPeopleCount
import com.example.ui.components.PrivacyBanner
import com.example.ui.theme.EmergencyContainer
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@Composable
fun RequestStatusScreen(
    request: PlacementRequest?,
    transportRequest: TransportRequest?,
    onRequestTransportClick: (PlacementRequest) -> Unit,
    onViewTransportStatusClick: () -> Unit,
    onSwitchToShelterDashboardClick: () -> Unit,
    onTryAnotherShelterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    if (request == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active placement request found.")
        }
        return
    }

    val scrollState = rememberScrollState()
    val isConfirmed = request.status in listOf("CONFIRMED", "TRANSPORT_REQUESTED", "TRANSPORT_ASSIGNED", "ON_THE_WAY", "COMPLETED")
    val isRejected = request.status == "REJECTED"
    val isPending = request.status in listOf("REQUEST_SENT", "PENDING")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("status_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Placement Status",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ticket #${request.id} • Session ${request.sessionId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Status Hero Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isConfirmed -> FreshGreenContainer
                    isRejected -> EmergencyContainer
                    else -> TealContainer
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
                                isConfirmed -> FreshGreen
                                isRejected -> MaterialTheme.colorScheme.error
                                else -> TealPrimary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isConfirmed -> Icons.Default.Check
                            isRejected -> Icons.Default.Cancel
                            else -> Icons.Default.HourglassTop
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when {
                        isConfirmed -> "PLACEMENT CONFIRMED"
                        isRejected -> "PLACEMENT NOT ACCEPTED"
                        else -> "REQUEST SENT (PENDING)"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = when {
                        isConfirmed -> FreshGreen
                        isRejected -> MaterialTheme.colorScheme.error
                        else -> TealPrimary
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when {
                        isConfirmed -> "Shelter staff has reviewed and confirmed your accommodation."
                        isRejected -> "The shelter was unable to accept this request. Please choose an alternate shelter."
                        else -> "Your request is currently in queue. Shelter staff are reviewing bed availability."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (isConfirmed && request.confirmationCode.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Intake Verification Code",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = request.confirmationCode,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp
                                ),
                                color = FreshGreen
                            )
                            Text(
                                text = "Show this upon arrival at reception",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Demo Helper Card for Hackathon Judges (PRD MVP Scenario Section 33)
        if (isPending) {
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
                        text = "To simulate shelter staff confirming this request, open the Shelter Dashboard below or from the top bar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSwitchToShelterDashboardClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NavySecondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("switch_to_shelter_dash_btn")
                    ) {
                        Text("Open Shelter Staff Dashboard", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stepper Visual Timeline (PRD Section 16 & 20)
        Text(
            text = "WORKFLOW PROGRESS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        TimelineStepItem(
            stepTitle = "Request Sent to Shelter",
            subtitle = "Anonymous ticket #${request.id} dispatched",
            isDone = true,
            isCurrent = request.status == "REQUEST_SENT"
        )
        TimelineStepItem(
            stepTitle = "Shelter Review & Confirmation",
            subtitle = if (isConfirmed) "Staff verified and confirmed space" else if (isRejected) "Rejected" else "Staff checking beds...",
            isDone = isConfirmed,
            isCurrent = request.status == "PENDING"
        )
        TimelineStepItem(
            stepTitle = "Placement Confirmed",
            subtitle = if (isConfirmed) "Intake code generated" else "Awaiting confirmation",
            isDone = isConfirmed,
            isCurrent = request.status == "CONFIRMED"
        )
        TimelineStepItem(
            stepTitle = "Transport Coordination",
            subtitle = when {
                transportRequest != null && transportRequest.status == "COMPLETED" -> "Arrived at shelter safely"
                transportRequest != null && transportRequest.status == "ON_THE_WAY" -> "Vehicle on the way"
                transportRequest != null && transportRequest.status == "ASSIGNED" -> "Vehicle assigned (ETA ${transportRequest.eta}m)"
                transportRequest != null -> "Transport requested"
                else -> "Available after confirmation"
            },
            isDone = transportRequest != null && transportRequest.status in listOf("ASSIGNED", "ON_THE_WAY", "COMPLETED"),
            isCurrent = request.status in listOf("TRANSPORT_REQUESTED", "TRANSPORT_ASSIGNED", "ON_THE_WAY")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Approved Shelter Details Card
        Text(
            text = "APPROVED SHELTER",
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NightShelter,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = request.shelterName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = request.shelterArea,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Occupant Details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatPeopleCount(request.totalPeople, request.adults, request.children),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Accessibility",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (request.wheelchairRequired) "Wheelchair accessible required" else "Standard accommodation",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Service Type",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = request.serviceType,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Transport Details Card (shown when transport request exists or was requested with placement)
        if (transportRequest != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TRANSPORT DETAILS",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = transportRequest.providerName ?: "Transport Provider",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = transportRequest.vehicleType ?: "Vehicle assigned",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Group details
                    TransportInfoRow("Passengers", formatPeopleCount(transportRequest.passengers, transportRequest.adults, transportRequest.children))
                    Spacer(modifier = Modifier.height(6.dp))

                    if (transportRequest.vehicleCapacity != null) {
                        TransportInfoRow("Vehicle Capacity", "${transportRequest.vehicleCapacity} seats")
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    TransportInfoRow("Accessibility", if (transportRequest.wheelchairRequired) "Wheelchair space required" else "Standard vehicle")
                    Spacer(modifier = Modifier.height(6.dp))

                    // Driver
                    if (transportRequest.driverName != null) {
                        TransportInfoRow("Driver", transportRequest.driverName)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // License Plate
                    if (transportRequest.vehiclePlate != null) {
                        TransportInfoRow("License Plate", transportRequest.vehiclePlate)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // ETA
                    TransportInfoRow("ETA", "${transportRequest.eta} minutes")
                    Spacer(modifier = Modifier.height(6.dp))

                    // Pickup Area
                    TransportInfoRow("Pickup Area", transportRequest.pickupArea)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Destination
                    TransportInfoRow("Destination", transportRequest.destinationShelterName)
                }
            }
        } else if (request.transportRequired || request.serviceType.contains("Transport")) {
            // Transport requested but awaiting dispatch
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TRANSPORT ASSISTANCE",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Transport Coordination Requested",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vehicle dispatch coordinates upon placement approval",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    TransportInfoRow("Passengers", formatPeopleCount(request.totalPeople, request.adults, request.children))
                    Spacer(modifier = Modifier.height(6.dp))
                    TransportInfoRow("Accessibility", if (request.wheelchairRequired) "Wheelchair accessible vehicle" else "Standard vehicle")
                    Spacer(modifier = Modifier.height(6.dp))
                    TransportInfoRow("Approved Destination", request.shelterName)
                    Spacer(modifier = Modifier.height(6.dp))
                    TransportInfoRow("Destination Area", request.shelterArea)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons according to state (PRD FR-11)
        if (isConfirmed) {
            if (transportRequest == null) {
                Button(
                    onClick = { onRequestTransportClick(request) },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("request_transport_cta_btn")
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Request Transport Assistance",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            } else {
                Button(
                    onClick = onViewTransportStatusClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("view_transport_status_cta_btn")
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Transport Status (${transportRequest.status})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else if (isRejected) {
            Button(
                onClick = onTryAnotherShelterClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Find Another Shelter",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TimelineStepItem(
    stepTitle: String,
    subtitle: String,
    isDone: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> FreshGreen
                        isCurrent -> TealPrimary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stepTitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isDone || isCurrent) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isDone || isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TransportInfoRow(label: String, value: String) {
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
