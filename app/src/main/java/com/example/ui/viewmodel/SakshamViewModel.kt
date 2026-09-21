package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SakshamDatabase
import com.example.data.model.AppNotification
import com.example.data.model.AvailabilityLog
import com.example.data.model.PlacementRequest
import com.example.data.model.Shelter
import com.example.data.model.SurvivorNeeds
import com.example.data.model.TransportProvider
import com.example.data.model.TransportRequest
import com.example.data.model.formatAdults
import com.example.data.model.formatChildren
import com.example.data.model.formatPeopleCount
import com.example.data.model.formatTotalPeople
import com.example.data.repository.SakshamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppRole(val roleKey: String, val label: String) {
    SURVIVOR("user", "Person Seeking Safety"),
    SHELTER_STAFF("shelter_staff", "Shelter Staff"),
    TRANSPORT_PROVIDER("transport_staff", "Transport Provider"),
    ADMIN("admin", "Administrator")
}

@OptIn(ExperimentalCoroutinesApi::class)
class SakshamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SakshamRepository

    init {
        val database = SakshamDatabase.getDatabase(application, viewModelScope)
        repository = SakshamRepository(database.sakshamDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    // User Authentication & Profile
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Ananya Sharma")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userContact = MutableStateFlow("ananya.s@example.com")
    val userContact: StateFlow<String> = _userContact.asStateFlow()

    // Role state
    private val _currentRole = MutableStateFlow(AppRole.SURVIVOR)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _userRole = MutableStateFlow("user")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    fun setRole(role: AppRole) {
        _currentRole.value = role
        _userRole.value = role.roleKey
    }

    private val _userWheelchairPref = MutableStateFlow(false)
    val userWheelchairPref: StateFlow<Boolean> = _userWheelchairPref.asStateFlow()

    private val _userChildPref = MutableStateFlow(false)
    val userChildPref: StateFlow<Boolean> = _userChildPref.asStateFlow()

    private val _userTransportPref = MutableStateFlow(true)
    val userTransportPref: StateFlow<Boolean> = _userTransportPref.asStateFlow()

    fun login(contact: String, pass: String) {
        if (contact.isNotBlank()) {
            _userContact.value = contact
            if (_userName.value.isBlank() || _userName.value == "User") {
                _userName.value = contact.substringBefore("@").replaceFirstChar { it.uppercase() }
            }
        }
        _isLoggedIn.value = true
        setRole(AppRole.SURVIVOR)
    }

    fun loginAdmin(contact: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (contact.isBlank()) {
            onError("Please enter admin identifier or email.")
            return
        }
        _userContact.value = contact
        _userName.value = "Saksham Administrator"
        _isLoggedIn.value = true
        setRole(AppRole.ADMIN)
        addNotification(
            title = "🛡️ Admin Session Active",
            message = "Logged in as Administrator with verification authority.",
            iconType = "CHECK"
        )
        onSuccess()
    }

    fun register(name: String, contact: String, pass: String) {
        if (name.isNotBlank()) _userName.value = name
        if (contact.isNotBlank()) _userContact.value = contact
        _isLoggedIn.value = true
        setRole(AppRole.SURVIVOR)
    }

    fun logout() {
        _isLoggedIn.value = false
        _activePlacementId.value = null
        _sessionId.value = generateAnonymousSessionId()
        setRole(AppRole.SURVIVOR)
    }

    fun updateProfile(
        name: String,
        contact: String,
        wheelchair: Boolean,
        child: Boolean,
        transport: Boolean
    ) {
        _userName.value = name
        _userContact.value = contact
        _userWheelchairPref.value = wheelchair
        _userChildPref.value = child
        _userTransportPref.value = transport
    }

    // Notifications (PRD Section 21)
    private val _notifications = MutableStateFlow<List<AppNotification>>(
        listOf(
            AppNotification(
                title = "✓ Saksham Safe System Active",
                message = "Anonymous encryption enabled for your session.",
                timestamp = "Just now",
                iconType = "CHECK"
            )
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun addNotification(title: String, message: String, iconType: String) {
        val newNotification = AppNotification(
            title = title,
            message = message,
            timestamp = "Just now",
            iconType = iconType
        )
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    // Anonymous Session
    private val _sessionId = MutableStateFlow(generateAnonymousSessionId())
    val sessionId: StateFlow<String> = _sessionId.asStateFlow()

    // Survivor Needs Selection
    private val _needs = MutableStateFlow(
        SurvivorNeeds(
            adults = 1,
            children = 0,
            userType = "1 Adult",
            serviceType = "Safe Shelter",
            needsChildren = false,
            needsWheelchair = false,
            generalArea = "Central District"
        )
    )
    val needs: StateFlow<SurvivorNeeds> = _needs.asStateFlow()

    // Filter toggle
    private val _showOnlyEligible = MutableStateFlow(true)
    val showOnlyEligible: StateFlow<Boolean> = _showOnlyEligible.asStateFlow()

    fun toggleShowOnlyEligible(onlyEligible: Boolean) {
        _showOnlyEligible.value = onlyEligible
    }

    // Shelters list reactively filtered by matching rules (Section 1 & 3)
    val shelters: StateFlow<List<Shelter>> = combine(
        repository.allShelters,
        _needs,
        _showOnlyEligible
    ) { all, currentNeeds, onlyEligible ->
        if (onlyEligible) {
            matchShelters(currentNeeds, all).sortedBy { it.travelTimeMinutes }
        } else {
            all.sortedBy { it.travelTimeMinutes }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allSheltersRaw: StateFlow<List<Shelter>> = repository.allShelters.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected shelter for details / request
    private val _selectedShelterId = MutableStateFlow<Long?>(1L)
    val selectedShelterId: StateFlow<Long?> = _selectedShelterId.asStateFlow()

    val selectedShelter: StateFlow<Shelter?> = _selectedShelterId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null) else repository.getShelterById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun selectShelter(id: Long) {
        _selectedShelterId.value = id
    }

    // Active placement request ID
    private val _activePlacementId = MutableStateFlow<Long?>(null)
    val activePlacementId: StateFlow<Long?> = _activePlacementId.asStateFlow()

    fun selectPlacementRequest(id: Long) {
        _activePlacementId.value = id
    }

    val activePlacementRequest: StateFlow<PlacementRequest?> = _activePlacementId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null) else repository.getPlacementRequestById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Active transport request for current placement
    val activeTransportRequest: StateFlow<TransportRequest?> = _activePlacementId.flatMapLatest { placementId ->
        if (placementId == null) MutableStateFlow(null) else repository.getTransportByPlacementId(placementId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // All placement requests (for Shelter staff dashboard)
    val allPlacementRequests: StateFlow<List<PlacementRequest>> = repository.allPlacementRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All transport providers
    val transportProviders: StateFlow<List<TransportProvider>> = repository.allTransportProviders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All transport requests (for Transport dashboard)
    val allTransportRequests: StateFlow<List<TransportRequest>> = repository.allTransportRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Availability logs
    val availabilityLogs: StateFlow<List<AvailabilityLog>> = repository.availabilityLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Shelter Staff selected shelter ID for bed updates
    private val _staffSelectedShelterId = MutableStateFlow(1L)
    val staffSelectedShelterId: StateFlow<Long> = _staffSelectedShelterId.asStateFlow()

    fun setStaffSelectedShelter(id: Long) {
        _staffSelectedShelterId.value = id
    }

    // --- Needs Configuration Actions ---
    fun updateAdults(count: Int) {
        val newAdults = count.coerceAtLeast(0)
        _needs.value = _needs.value.copy(adults = newAdults)
    }

    fun updateChildren(count: Int) {
        val newChildren = count.coerceAtLeast(0)
        _needs.value = _needs.value.copy(
            children = newChildren,
            needsChildren = newChildren > 0
        )
    }

    fun setTransportRequired(required: Boolean) {
        _needs.value = _needs.value.copy(
            transportRequired = required,
            serviceType = if (required) "Shelter + Transport Assistance" else "Safe Shelter"
        )
    }

    fun toggleWheelchairRequirement(enabled: Boolean) {
        _needs.value = _needs.value.copy(needsWheelchair = enabled)
    }

    fun toggleChildRequirement(enabled: Boolean) {
        // Required only if children > 0 or explicitly set
        val effective = if (_needs.value.children > 0) true else enabled
        _needs.value = _needs.value.copy(needsChildren = effective)
    }

    fun updateUserType(type: String) {
        _needs.value = _needs.value.copy(userType = type)
    }

    fun updateServiceType(type: String) {
        _needs.value = _needs.value.copy(serviceType = type)
    }

    fun toggleChildAccommodation(enabled: Boolean) {
        toggleChildRequirement(enabled)
    }

    fun toggleWheelchairAccommodation(enabled: Boolean) {
        toggleWheelchairRequirement(enabled)
    }

    fun updateGeneralArea(area: String) {
        _needs.value = _needs.value.copy(generalArea = area)
    }

    fun resetNeeds() {
        _needs.value = SurvivorNeeds(
            adults = 1,
            children = 0,
            userType = "1 Adult",
            serviceType = "Safe Shelter",
            needsChildren = false,
            needsWheelchair = false,
            generalArea = "Central District"
        )
    }

    // --- Survivor Flow Actions ---
    fun submitPlacementRequest(shelter: Shelter, transportRequested: Boolean, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val currentNeeds = _needs.value.copy(transportRequired = transportRequested)
            val request = PlacementRequest(
                sessionId = _sessionId.value,
                shelterId = shelter.id,
                shelterName = shelter.name,
                shelterArea = shelter.area,
                adults = currentNeeds.adults,
                children = currentNeeds.children,
                totalPeople = currentNeeds.totalPeople,
                wheelchairRequired = currentNeeds.wheelchairRequired,
                transportRequired = transportRequested,
                needsChildren = currentNeeds.childrenRequired,
                needsWheelchair = currentNeeds.wheelchairRequired,
                serviceType = if (transportRequested) "Shelter + Transport Assistance" else "Safe Shelter",
                status = "PENDING",
                intakeNotes = "Safety intake for ${formatPeopleCount(currentNeeds.totalPeople, currentNeeds.adults, currentNeeds.children)}."
            )
            val newId = repository.createPlacementRequest(request)
            _activePlacementId.value = newId
            addNotification(
                title = "✓ Placement request sent",
                message = "Your request for ${formatTotalPeople(currentNeeds.totalPeople)} has been sent to ${shelter.name}.",
                iconType = "CHECK"
            )
            onSuccess(newId)
        }
    }

    fun requestTransport(placementId: Long, shelterName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentNeeds = _needs.value
            val req = TransportRequest(
                placementRequestId = placementId,
                adults = currentNeeds.adults,
                children = currentNeeds.children,
                passengers = currentNeeds.totalPeople,
                wheelchairRequired = currentNeeds.wheelchairRequired,
                pickupArea = currentNeeds.generalArea,
                destinationShelterName = shelterName,
                status = "REQUESTED",
                eta = if (currentNeeds.wheelchairRequired) 15 else 8
            )
            repository.createTransportRequest(req)
            repository.updatePlacementStatus(placementId, "TRANSPORT_REQUESTED")
            addNotification(
                title = "🚗 Transport requested",
                message = "Coordinating vehicle dispatch for ${currentNeeds.totalPeople} passengers to $shelterName.",
                iconType = "TRANSPORT"
            )
            onSuccess()
        }
    }

    // --- Shelter Staff Actions ---
    fun confirmPlacement(requestId: Long) {
        viewModelScope.launch {
            val verificationCode = "SK-CF${Random.nextInt(1000, 9999)}"
            repository.confirmPlacementRequest(requestId, verificationCode)
            val req = repository.getPlacementRequestByIdDirect(requestId)
            if (req != null && req.transportRequired) {
                val tReq = TransportRequest(
                    placementRequestId = requestId,
                    adults = req.adults,
                    children = req.children,
                    passengers = req.totalPeople,
                    wheelchairRequired = req.wheelchairRequired,
                    pickupArea = "Central District",
                    destinationShelterName = req.shelterName,
                    status = "REQUESTED",
                    eta = if (req.wheelchairRequired) 15 else 8
                )
                repository.createTransportRequest(tReq)
                repository.updatePlacementStatus(requestId, "TRANSPORT_REQUESTED")
            }
            addNotification(
                title = "✓ Shelter confirmed",
                message = "Your placement has been confirmed.",
                iconType = "CHECK"
            )
        }
    }

    fun rejectPlacement(requestId: Long) {
        viewModelScope.launch {
            repository.rejectPlacementRequest(requestId)
        }
    }

    fun updateShelterBeds(shelterId: Long, newBeds: Int, shelterName: String) {
        viewModelScope.launch {
            val clamped = newBeds.coerceAtLeast(0)
            repository.updateShelterBeds(shelterId, clamped, shelterName)
        }
    }

    // --- Transport Provider Actions ---
    fun assignVehicleToRequest(
        transportId: Long,
        placementRequestId: Long,
        provider: TransportProvider,
        simulatedEta: Int
    ) {
        viewModelScope.launch {
            repository.assignTransportVehicle(
                transportId = transportId,
                placementRequestId = placementRequestId,
                provider = provider,
                eta = simulatedEta
            )
            addNotification(
                title = "🚗 Transport assigned",
                message = "${provider.name} (${provider.vehicleType}) assigned with ~${simulatedEta}m ETA.",
                iconType = "TRANSPORT"
            )
        }
    }

    fun updateTransportStatus(transportId: Long, placementRequestId: Long, status: String) {
        viewModelScope.launch {
            repository.updateTransportStatus(transportId, placementRequestId, status)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            _sessionId.value = generateAnonymousSessionId()
            _activePlacementId.value = null
        }
    }

    fun quickExit() {
        // Instant privacy action: generates new session, clears active tracking
        _sessionId.value = generateAnonymousSessionId()
        _activePlacementId.value = null
    }

    // --- Admin Verification Actions ---
    fun approveShelter(shelterId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val shelter = allSheltersRaw.value.find { it.id == shelterId }
            if (shelter != null && shelter.verificationStatus == "approved" && shelter.verified) {
                onResult(false, "This shelter is already approved.")
                return@launch
            }
            repository.approveShelter(shelterId)
            addNotification(
                title = "Shelter Approved ✓",
                message = "${shelter?.name ?: "Shelter"} is now verified and can appear in survivor searches.",
                iconType = "CHECK"
            )
            onResult(true, "Shelter Approved ✓\nThis shelter is now verified and can appear in survivor searches.")
        }
    }

    fun rejectShelter(shelterId: Long, reason: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val shelter = allSheltersRaw.value.find { it.id == shelterId }
            val effectiveReason = if (reason.isBlank()) "Verification information incomplete" else reason
            repository.rejectShelter(shelterId, effectiveReason)
            addNotification(
                title = "Shelter Verification Rejected",
                message = "${shelter?.name ?: "Shelter"} was rejected: $effectiveReason",
                iconType = "INFO"
            )
            onResult(true, "Shelter rejected. It will not appear in survivor searches.")
        }
    }

    private fun generateAnonymousSessionId(): String {
        return "SK-${Random.nextInt(1000, 9999)}"
    }
}

/**
 * Core matching rule (Section 11 & 12 of Saksham PRD):
 *
 * 1. Admin verification: verification_status == "approved" AND verified == true
 * 2. Shelter must currently be available: availability_status == "available"
 * 3. Capacity is ALWAYS required: available >= total_people
 * 4. Children only matter if the user has children (children > 0)
 * 5. Wheelchair only matters if requested (wheelchair_required == true)
 * 6. Shelters with additional features are never excluded for users who don't need them.
 */
fun matchShelters(survivorNeeds: SurvivorNeeds, shelters: List<Shelter>): List<Shelter> {
    return shelters.filter { shelter ->
        // Admin verification
        if (shelter.verificationStatus != "approved") {
            return@filter false
        }

        if (!shelter.verified) {
            return@filter false
        }

        // Shelter must currently be available
        if (shelter.availabilityStatus != "available") {
            return@filter false
        }

        // Capacity
        if (shelter.availableBeds < survivorNeeds.totalPeople) {
            return@filter false
        }

        // Children only matter if the user has children
        if (survivorNeeds.children > 0 && !shelter.acceptsChildren) {
            return@filter false
        }

        // Wheelchair only matters if requested
        if (survivorNeeds.wheelchairRequired && !shelter.acceptsWheelchair) {
            return@filter false
        }

        return@filter true
    }
}
