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
        WHERE available_beds > 0 
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
    @Query("SELECT * FROM transport_providers ORDER BY eta_minutes ASC")
    fun getAllTransportProviders(): Flow<List<TransportProvider>>

    @Query("SELECT * FROM transport_providers WHERE wheelchair_accessible = 1 ORDER BY eta_minutes ASC")
    fun getWheelchairTransportProviders(): Flow<List<TransportProvider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportProviders(providers: List<TransportProvider>)

    @Query("SELECT COUNT(*) FROM transport_providers")
    suspend fun getTransportProviderCount(): Int

    // --- TRANSPORT REQUESTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransportRequest(request: TransportRequest): Long

    @Query("""
        UPDATE transport_requests 
        SET status = :status, 
            provider_id = :providerId, 
            provider_name = :providerName, 
            vehicle_type = :vehicleType, 
            driver_name = :driverName, 
            vehicle_plate = :vehiclePlate, 
            vehicle_capacity = :vehicleCapacity,
            eta = :eta 
        WHERE id = :id
    """)
    suspend fun assignTransportVehicle(
        id: Long,
        status: String,
        providerId: Long,
        providerName: String,
        vehicleType: String,
        driverName: String,
        vehiclePlate: String,
        vehicleCapacity: Int,
        eta: Int
    )

    @Query("UPDATE transport_requests SET status = :status WHERE id = :id")
    suspend fun updateTransportStatus(id: Long, status: String)

    @Query("SELECT * FROM transport_requests WHERE placement_request_id = :placementRequestId LIMIT 1")
    fun getTransportByPlacementId(placementRequestId: Long): Flow<TransportRequest?>

    @Query("SELECT * FROM transport_requests ORDER BY created_at DESC")
    fun getAllTransportRequests(): Flow<List<TransportRequest>>

    @Query("SELECT * FROM transport_requests WHERE id = :id")
    fun getTransportRequestById(id: Long): Flow<TransportRequest?>
}
