package com.example.data.repository

import com.example.data.local.SakshamDao
import com.example.data.local.SakshamDatabase
import com.example.data.model.AvailabilityLog
import com.example.data.model.PlacementRequest
import com.example.data.model.Shelter
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SakshamRepository(private val dao: SakshamDao) {

    val allShelters: Flow<List<Shelter>> = dao.getAllShelters()

    fun getEligibleShelters(needsChild: Boolean, needsWheelchair: Boolean): Flow<List<Shelter>> {
        return dao.getEligibleShelters(
            needsChild = if (needsChild) 1 else 0,
            needsWheelchair = if (needsWheelchair) 1 else 0
        )
    }

    fun getShelterById(id: Long): Flow<Shelter?> = dao.getShelterById(id)

    suspend fun updateShelterBeds(shelterId: Long, beds: Int, shelterName: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateShelterBeds(shelterId, beds, now)
        dao.insertAvailabilityLog(
            AvailabilityLog(
                shelterId = shelterId,
                shelterName = shelterName,
                availableBeds = beds,
                updatedAt = now
            )
        )
    }

    suspend fun updateShelterVerification(
        shelterId: Long,
        status: String,
        verified: Boolean,
        rejectionReason: String? = null
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.updateShelterVerification(shelterId, status, verified, rejectionReason, now)
    }

    suspend fun approveShelter(shelterId: Long) = updateShelterVerification(
        shelterId = shelterId,
        status = "approved",
        verified = true,
        rejectionReason = null
    )

    suspend fun rejectShelter(shelterId: Long, reason: String) = updateShelterVerification(
        shelterId = shelterId,
        status = "rejected",
        verified = false,
        rejectionReason = reason
    )

    fun getSheltersByVerificationStatus(status: String): Flow<List<Shelter>> =
        dao.getSheltersByVerificationStatus(status)


    suspend fun createPlacementRequest(request: PlacementRequest): Long = withContext(Dispatchers.IO) {
        dao.insertPlacementRequest(request)
    }

    suspend fun approvePlacement(requestId: Long, code: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.approvePlacementRequest(requestId, now, code)
    }

    suspend fun confirmPlacementRequest(requestId: Long, code: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.approvePlacementRequest(requestId, now, code)
    }

    suspend fun rejectPlacement(requestId: Long, reason: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.rejectPlacementRequest(requestId, now, reason)
    }

    suspend fun rejectPlacementRequest(requestId: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.rejectPlacementRequest(requestId, now, "Shelter currently cannot accommodate this request.")
    }

    suspend fun updatePlacementStatus(requestId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updatePlacementRequestStatus(requestId, status)
    }

    fun getPlacementRequestsBySession(sessionId: String): Flow<List<PlacementRequest>> =
        dao.getRequestsBySession(sessionId)

    fun getPlacementRequestById(id: Long): Flow<PlacementRequest?> =
        dao.getPlacementRequestById(id)

    suspend fun getPlacementRequestByIdDirect(id: Long): PlacementRequest? = withContext(Dispatchers.IO) {
        dao.getPlacementRequestByIdDirect(id)
    }

    val allPlacementRequests: Flow<List<PlacementRequest>> = dao.getAllPlacementRequests()

    fun getPlacementRequestsForShelter(shelterId: Long): Flow<List<PlacementRequest>> =
        dao.getPlacementRequestsForShelter(shelterId)

    val allTransportProviders: Flow<List<TransportProvider>> = dao.getAllTransportProviders()

    fun getTransportProviders(wheelchairRequired: Boolean): Flow<List<TransportProvider>> {
        return if (wheelchairRequired) {
            dao.getWheelchairTransportProviders()
        } else {
            dao.getAllTransportProviders()
        }
    }

    suspend fun createTransportRequest(request: TransportRequest): Long = withContext(Dispatchers.IO) {
        val existing = dao.getTransportRequestByPlacementIdDirect(request.placementRequestId)
        if (existing != null) {
            return@withContext existing.id
        }
        dao.insertTransportRequest(request)
    }

    suspend fun getTransportRequestByPlacementIdDirect(placementRequestId: Long): TransportRequest? = withContext(Dispatchers.IO) {
        dao.getTransportRequestByPlacementIdDirect(placementRequestId)
    }

    suspend fun assignTransportVehicle(
        transportId: Long,
        placementRequestId: Long,
        provider: TransportProvider,
        eta: Int
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.assignTransportVehicle(
            id = transportId,
            status = "ASSIGNED",
            providerId = provider.id,
            providerName = provider.name,
            assignedVehicleId = provider.id,
            assignedVehicleName = provider.name,
            vehicleType = provider.vehicleType,
            driverName = provider.driverName,
            vehiclePlate = provider.vehiclePlate,
            vehicleCapacity = provider.passengerCapacity,
            eta = eta,
            assignedAt = now
        )
        // Mark vehicle as not available while assigned
        dao.updateVehicleAvailability(provider.id, available = false, status = "ASSIGNED")
        // Update placement request status as well
        dao.updatePlacementRequestStatus(placementRequestId, "TRANSPORT_ASSIGNED")
    }

    suspend fun declineTransportRequest(transportId: Long, reason: String) = withContext(Dispatchers.IO) {
        dao.declineTransportRequest(transportId, reason)
    }

    suspend fun updateTransportStatus(
        transportId: Long,
        placementRequestId: Long,
        providerId: Long?,
        status: String
    ) = withContext(Dispatchers.IO) {
        dao.updateTransportStatus(transportId, status)
        when (status) {
            "ON_THE_WAY" -> dao.updatePlacementRequestStatus(placementRequestId, "ON_THE_WAY")
            "ARRIVED" -> dao.updatePlacementRequestStatus(placementRequestId, "ARRIVED")
            "COMPLETED" -> {
                dao.updatePlacementRequestStatus(placementRequestId, "COMPLETED")
                if (providerId != null) {
                    // Make vehicle available again upon completion
                    dao.updateVehicleAvailability(providerId, available = true, status = "Available")
                }
            }
        }
    }

    suspend fun updateVehicleAvailability(providerId: Long, available: Boolean) = withContext(Dispatchers.IO) {
        val statusStr = if (available) "Available" else "Unavailable"
        dao.updateVehicleAvailability(providerId, available, statusStr)
    }

    suspend fun updateTransportEta(transportId: Long, eta: Int) = withContext(Dispatchers.IO) {
        dao.updateTransportEta(transportId, eta)
    }

    fun getTransportByPlacementId(placementRequestId: Long): Flow<TransportRequest?> =
        dao.getTransportByPlacementId(placementRequestId)

    val allTransportRequests: Flow<List<TransportRequest>> = dao.getAllTransportRequests()

    val availabilityLogs: Flow<List<AvailabilityLog>> = dao.getAllAvailabilityLogs()

    suspend fun resetDemoData() = withContext(Dispatchers.IO) {
        SakshamDatabase.populateInitialData(dao)
    }

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        if (dao.getShelterCount() == 0) {
            SakshamDatabase.populateInitialData(dao)
        }
    }
}
