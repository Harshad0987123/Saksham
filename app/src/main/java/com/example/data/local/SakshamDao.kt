package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AvailabilityLog
import com.example.data.model.PlacementRequest
import com.example.data.model.Shelter
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface SakshamDao {

    // --- SHELTERS ---
    @Query("SELECT * FROM shelters ORDER BY travel_time_minutes ASC")
    fun getAllShelters(): Flow<List<Shelter>>

    @Query("SELECT * FROM shelters WHERE id = :id")
    fun getShelterById(id: Long): Flow<Shelter?>

    @Query("""
        SELECT * FROM shelters 
        WHERE verification_status = 'approved'
          AND verified = 1
          AND availability_status = 'available'
          AND available_beds > 0 
          AND (:needsChild = 0 OR accepts_children = 1)
          AND (:needsWheelchair = 0 OR accepts_wheelchair = 1)
        ORDER BY travel_time_minutes ASC
    """)
    fun getEligibleShelters(needsChild: Int, needsWheelchair: Int): Flow<List<Shelter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelters(shelters: List<Shelter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelter(shelter: Shelter): Long

    @Query("UPDATE shelters SET available_beds = :beds, last_updated = :updatedAt WHERE id = :id")
    suspend fun updateShelterBeds(id: Long, beds: Int, updatedAt: Long)

    @Query("UPDATE shelters SET verification_status = :status, verified = :verified, rejection_reason = :rejectionReason, last_updated = :updatedAt WHERE id = :id")
    suspend fun updateShelterVerification(id: Long, status: String, verified: Boolean, rejectionReason: String?, updatedAt: Long)

    @Query("SELECT * FROM shelters WHERE verification_status = :status ORDER BY created_at DESC")
    fun getSheltersByVerificationStatus(status: String): Flow<List<Shelter>>

    @Query("SELECT COUNT(*) FROM shelters")
    suspend fun getShelterCount(): Int

    // --- AVAILABILITY LOGS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailabilityLog(log: AvailabilityLog)

    @Query("SELECT * FROM availability_logs ORDER BY updated_at DESC LIMIT 50")
    fun getAllAvailabilityLogs(): Flow<List<AvailabilityLog>>

    // --- PLACEMENT REQUESTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacementRequest(request: PlacementRequest): Long

    @Query("UPDATE placement_requests SET status = :status WHERE id = :id")
    suspend fun updatePlacementRequestStatus(id: Long, status: String)

    @Query("UPDATE placement_requests SET status = 'APPROVED', approved_at = :approvedAt, confirmation_code = :code WHERE id = :id")
    suspend fun approvePlacementRequest(id: Long, approvedAt: Long, code: String)

    @Query("UPDATE placement_requests SET status = 'REJECTED', rejected_at = :rejectedAt, rejection_reason = :reason WHERE id = :id")
    suspend fun rejectPlacementRequest(id: Long, rejectedAt: Long, reason: String)

    @Query("UPDATE placement_requests SET status = :status, confirmation_code = :code WHERE id = :id")
    suspend fun confirmPlacementRequest(id: Long, status: String, code: String)

    @Query("SELECT * FROM placement_requests WHERE session_id = :sessionId ORDER BY created_at DESC")
    fun getRequestsBySession(sessionId: String): Flow<List<PlacementRequest>>

    @Query("SELECT * FROM placement_requests WHERE id = :id")
    fun getPlacementRequestById(id: Long): Flow<PlacementRequest?>

    @Query("SELECT * FROM placement_requests WHERE id = :id")
    suspend fun getPlacementRequestByIdDirect(id: Long): PlacementRequest?

    @Query("SELECT * FROM placement_requests ORDER BY created_at DESC")
    fun getAllPlacementRequests(): Flow<List<PlacementRequest>>

    @Query("SELECT * FROM placement_requests WHERE shelter_id = :shelterId ORDER BY created_at DESC")
    fun getPlacementRequestsForShelter(shelterId: Long): Flow<List<PlacementRequest>>

    // --- TRANSPORT PROVIDERS ---
    @Query("SELECT * FROM transport_providers ORDER BY passenger_capacity ASC")
    fun getAllTransportProviders(): Flow<List<TransportProvider>>

    @Query("SELECT * FROM transport_providers WHERE wheelchair_accessible = 1 ORDER BY passenger_capacity ASC")
    fun getWheelchairTransportProviders(): Flow<List<TransportProvider>>

    @Query("SELECT * FROM transport_providers WHERE id = :id")
    fun getTransportProviderById(id: Long): Flow<TransportProvider?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportProviders(providers: List<TransportProvider>)

    @Query("SELECT COUNT(*) FROM transport_providers")
    suspend fun getTransportProviderCount(): Int

    @Query("UPDATE transport_providers SET available = :available, status = :status WHERE id = :providerId")
    suspend fun updateVehicleAvailability(providerId: Long, available: Boolean, status: String)

    // --- TRANSPORT REQUESTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportRequest(request: TransportRequest): Long

    @Query("""
        UPDATE transport_requests 
        SET status = :status, 
            provider_id = :providerId, 
            provider_name = :providerName, 
            assigned_vehicle_id = :assignedVehicleId,
            assigned_vehicle_name = :assignedVehicleName,
            vehicle_type = :vehicleType, 
            driver_name = :driverName, 
            vehicle_plate = :vehiclePlate, 
            vehicle_capacity = :vehicleCapacity,
            eta = :eta,
            assigned_at = :assignedAt
        WHERE id = :id
    """)
    suspend fun assignTransportVehicle(
        id: Long,
        status: String,
        providerId: Long,
        providerName: String,
        assignedVehicleId: Long,
        assignedVehicleName: String,
        vehicleType: String,
        driverName: String,
        vehiclePlate: String,
        vehicleCapacity: Int,
        eta: Int,
        assignedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE transport_requests SET status = :status WHERE id = :id")
    suspend fun updateTransportStatus(id: Long, status: String)

    @Query("UPDATE transport_requests SET status = 'DECLINED', rejection_reason = :reason WHERE id = :id")
    suspend fun declineTransportRequest(id: Long, reason: String)

    @Query("UPDATE transport_requests SET eta = :eta WHERE id = :id")
    suspend fun updateTransportEta(id: Long, eta: Int)

    @Query("SELECT * FROM transport_requests WHERE placement_request_id = :placementRequestId LIMIT 1")
    fun getTransportByPlacementId(placementRequestId: Long): Flow<TransportRequest?>

    @Query("SELECT * FROM transport_requests WHERE placement_request_id = :placementRequestId LIMIT 1")
    suspend fun getTransportRequestByPlacementIdDirect(placementRequestId: Long): TransportRequest?

    @Query("SELECT * FROM transport_requests WHERE placement_request_id = :placementRequestId AND status IN ('REQUESTED', 'ASSIGNED', 'ON_THE_WAY') LIMIT 1")
    suspend fun getActiveTransportForPlacement(placementRequestId: Long): TransportRequest?

    @Query("SELECT * FROM transport_requests ORDER BY created_at DESC")
    fun getAllTransportRequests(): Flow<List<TransportRequest>>

    @Query("SELECT * FROM transport_requests WHERE id = :id")
    fun getTransportRequestById(id: Long): Flow<TransportRequest?>
}
