package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Shelter entity matching Table 1 of Saksham PRD
 */
@Entity(tableName = "shelters")
data class Shelter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val area: String,
    @ColumnInfo(name = "available_beds")
    val availableBeds: Int,
    @ColumnInfo(name = "total_beds")
    val totalBeds: Int = 10,
    @ColumnInfo(name = "accepts_children")
    val acceptsChildren: Boolean,
    @ColumnInfo(name = "accepts_wheelchair")
    val acceptsWheelchair: Boolean,
    val status: String = "OPEN",
    @ColumnInfo(name = "verification_status", defaultValue = "'approved'")
    val verificationStatus: String = "approved", // "pending", "approved", "rejected"
    @ColumnInfo(name = "verified", defaultValue = "1")
    val verified: Boolean = true,
    @ColumnInfo(name = "availability_status", defaultValue = "'available'")
    val availabilityStatus: String = "available", // "available", "unavailable"
    @ColumnInfo(name = "travel_time_minutes")
    val travelTimeMinutes: Int = 15,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String? = null,
    @ColumnInfo(name = "verification_notes")
    val verificationNotes: String = "NGO Registration & Safety Compliance Verified",
    val features: String = "Trauma-informed staff, 24/7 Security, Emergency essentials",
    @ColumnInfo(name = "staff_contact")
    val staffContact: String = "Staff On-Duty Helpline"
) {
    val capacity: Int get() = totalBeds
    val available: Int get() = availableBeds
    val accepts_children: Boolean get() = acceptsChildren
    val wheelchair_accessible: Boolean get() = acceptsWheelchair
    val verification_status: String get() = verificationStatus
    val availability_status: String get() = availabilityStatus
    val rejection_reason: String? get() = rejectionReason
    val last_updated: Long get() = lastUpdated
    val created_at: Long get() = createdAt
}


/**
 * Transport Provider entity matching Table 2 of Saksham PRD
 */
@Entity(tableName = "transport_providers")
data class TransportProvider(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "vehicle_type")
    val vehicleType: String,
    @ColumnInfo(name = "wheelchair_accessible")
    val wheelchairAccessible: Boolean,
    @ColumnInfo(name = "passenger_capacity")
    val passengerCapacity: Int = 4,
    val available: Boolean = true,
    val status: String = "Available",
    @ColumnInfo(name = "driver_name")
    val driverName: String,
    @ColumnInfo(name = "vehicle_plate")
    val vehiclePlate: String,
    @ColumnInfo(name = "eta_minutes")
    val etaMinutes: Int = 12
) {
    val vehicleId: Long get() = id
    val vehicleName: String get() = name
    val capacity: Int get() = passengerCapacity
    val currentStatus: String get() = status
}

/**
 * Placement Request entity matching Table 3 of Saksham PRD
 */
@Entity(tableName = "placement_requests")
data class PlacementRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    @ColumnInfo(name = "shelter_id")
    val shelterId: Long,
    @ColumnInfo(name = "shelter_name")
    val shelterName: String,
    @ColumnInfo(name = "shelter_area")
    val shelterArea: String,
    @ColumnInfo(name = "adults")
    val adults: Int = 1,
    @ColumnInfo(name = "children")
    val children: Int = 0,
    @ColumnInfo(name = "total_people")
    val totalPeople: Int = 1,
    @ColumnInfo(name = "wheelchair_required")
    val wheelchairRequired: Boolean = false,
    @ColumnInfo(name = "transport_required")
    val transportRequired: Boolean = false,
    @ColumnInfo(name = "pickup_area")
    val pickupArea: String = "Central District",
    @ColumnInfo(name = "needs_children")
    val needsChildren: Boolean = false,
    @ColumnInfo(name = "needs_wheelchair")
    val needsWheelchair: Boolean = false,
    @ColumnInfo(name = "service_type")
    val serviceType: String = "Safe Shelter",
    val status: String = "REQUESTED", // REQUESTED, PENDING, APPROVED, REJECTED, TRANSPORT_REQUESTED, COMPLETED
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "approved_at")
    val approvedAt: Long? = null,
    @ColumnInfo(name = "rejected_at")
    val rejectedAt: Long? = null,
    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String? = null,
    @ColumnInfo(name = "confirmation_code")
    val confirmationCode: String = "",
    @ColumnInfo(name = "intake_notes")
    val intakeNotes: String = ""
) {
    val userId: String get() = sessionId
}

/**
 * Transport Request entity matching Table 4 of Saksham PRD
 */
@Entity(tableName = "transport_requests")
data class TransportRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "placement_request_id")
    val placementRequestId: Long,
    @ColumnInfo(name = "user_id")
    val userId: String = "",
    @ColumnInfo(name = "adults")
    val adults: Int = 1,
    @ColumnInfo(name = "children")
    val children: Int = 0,
    @ColumnInfo(name = "passengers")
    val passengers: Int = 1,
    @ColumnInfo(name = "vehicle_capacity")
    val vehicleCapacity: Int? = null,
    @ColumnInfo(name = "provider_id")
    val providerId: Long? = null,
    @ColumnInfo(name = "provider_name")
    val providerName: String? = null,
    @ColumnInfo(name = "assigned_vehicle_id")
    val assignedVehicleId: Long? = null,
    @ColumnInfo(name = "assigned_vehicle_name")
    val assignedVehicleName: String? = null,
    @ColumnInfo(name = "vehicle_type")
    val vehicleType: String? = null,
    @ColumnInfo(name = "driver_name")
    val driverName: String? = null,
    @ColumnInfo(name = "vehicle_plate")
    val vehiclePlate: String? = null,
    @ColumnInfo(name = "wheelchair_required")
    val wheelchairRequired: Boolean = false,
    val status: String = "REQUESTED", // REQUESTED, ASSIGNED, ON_THE_WAY, ARRIVED, DECLINED, COMPLETED
    val eta: Int = 15,
    @ColumnInfo(name = "pickup_area")
    val pickupArea: String = "Current General Area",
    @ColumnInfo(name = "destination_shelter_id")
    val destinationShelterId: Long = 1,
    @ColumnInfo(name = "destination_shelter_name")
    val destinationShelterName: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "assigned_at")
    val assignedAt: Long? = null,
    @ColumnInfo(name = "rejection_reason")
    val rejectionReason: String? = null
) {
    val totalPeople: Int get() = passengers
}

/**
 * Availability Log entity matching Table 5 of Saksham PRD
 */
@Entity(tableName = "availability_logs")
data class AvailabilityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "shelter_id")
    val shelterId: Long,
    @ColumnInfo(name = "shelter_name")
    val shelterName: String,
    @ColumnInfo(name = "available_beds")
    val availableBeds: Int,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Needs selection state for user seeking safety
 */
data class SurvivorNeeds(
    val adults: Int = 1,
    val children: Int = 0,
    val userType: String = "Me",
    val serviceType: String = "Safe Shelter",
    val needsChildren: Boolean = false,
    val needsWheelchair: Boolean = false,
    val transportRequired: Boolean = false,
    val generalArea: String = "Central Area"
) {
    val totalPeople: Int
        get() = adults + children

    val childrenRequired: Boolean
        get() = children > 0

    val wheelchairRequired: Boolean
        get() = needsWheelchair
}

/**
 * Dynamic grammar helpers for people counts (Section 8)
 */
fun formatTotalPeople(count: Int): String =
    if (count == 1) "1 person" else "$count people"

fun formatAdults(count: Int): String =
    if (count == 1) "1 adult" else "$count adults"

fun formatChildren(count: Int): String =
    if (count == 1) "1 child" else "$count children"

fun formatPassengerBreakdown(adults: Int, children: Int): String =
    if (children > 0) "${formatAdults(adults)} + ${formatChildren(children)}" else formatAdults(adults)

fun formatPeopleCount(total: Int, adults: Int, children: Int): String =
    "${formatTotalPeople(total)} (${formatAdults(adults)}, ${formatChildren(children)})"


/**
 * Freshness status calculation based on PRD Section 13
 */
enum class FreshnessLevel {
    RECENTLY_CONFIRMED, // < 15 minutes
    UPDATED_RECENTLY,    // 15 - 30 minutes
    NEEDS_CONFIRMATION  // > 30 minutes
}

fun calculateFreshness(lastUpdatedMs: Long, currentMs: Long = System.currentTimeMillis()): Pair<FreshnessLevel, String> {
    val diffMinutes = ((currentMs - lastUpdatedMs).coerceAtLeast(0L) / (1000 * 60)).toInt()
    return when {
        diffMinutes < 15 -> FreshnessLevel.RECENTLY_CONFIRMED to if (diffMinutes <= 1) "Updated just now" else "Updated ${diffMinutes}m ago"
        diffMinutes <= 30 -> FreshnessLevel.UPDATED_RECENTLY to "Updated ${diffMinutes}m ago"
        else -> FreshnessLevel.NEEDS_CONFIRMATION to "Updated ${diffMinutes}m ago"
    }
}

/**
 * In-app Notification entity for Section 21 of PRD
 */
data class AppNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val timestamp: String,
    val iconType: String = "INFO", // "CHECK", "TRANSPORT", "ALERT", "INFO"
    val isRead: Boolean = false
)

