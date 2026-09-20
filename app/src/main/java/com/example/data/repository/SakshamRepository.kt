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

    suspend fun createPlacementRequest(request: PlacementRequest): Long = withContext(Dispatchers.IO) {
        dao.insertPlacementRequest(request)
    }

    suspend fun confirmPlacementRequest(requestId: Long, code: String) = withContext(Dispatchers.IO) {
        dao.confirmPlacementRequest(requestId, "CONFIRMED", code)
    }

    suspend fun rejectPlacementRequest(requestId: Long) = withContext(Dispatchers.IO) {
        dao.updatePlacementRequestStatus(requestId, "REJECTED")
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
        dao.insertTransportRequest(request)
    }

    suspend fun assignTransportVehicle(
        transportId: Long,
        placementRequestId: Long,
        provider: TransportProvider,
        eta: Int
    ) = withContext(Dispatchers.IO) {
        dao.assignTransportVehicle(
            id = transportId,
            status = "ASSIGNED",
            providerId = provider.id,
            providerName = provider.name,
            vehicleType = provider.vehicleType,
            driverName = provider.driverName,
            vehiclePlate = provider.vehiclePlate,
            vehicleCapacity = provider.passengerCapacity,
            eta = eta
        )
        // Update placement request status as well
        dao.updatePlacementRequestStatus(placementRequestId, "TRANSPORT_ASSIGNED")
    }

    suspend fun updateTransportStatus(transportId: Long, placementRequestId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updateTransportStatus(transportId, status)
        when (status) {
            "ON_THE_WAY" -> dao.updatePlacementRequestStatus(placementRequestId, "ON_THE_WAY")
            "ARRIVED" -> dao.updatePlacementRequestStatus(placementRequestId, "ARRIVED")
            "COMPLETED" -> dao.updatePlacementRequestStatus(placementRequestId, "COMPLETED")
        }
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
