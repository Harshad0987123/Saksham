package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.Shelter
import com.example.ui.components.SakshamBottomNavigation
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.screens.admin.AdminShelterReviewScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.more.AboutScreen
import com.example.ui.screens.more.HelpSupportScreen
import com.example.ui.screens.more.MoreScreen
import com.example.ui.screens.more.MyAccountScreen
import com.example.ui.screens.more.NotificationsScreen
import com.example.ui.screens.more.PrivacyInfoScreen
import com.example.ui.screens.more.SettingsScreen
import com.example.ui.screens.staff.ShelterDashboardScreen
import com.example.ui.screens.survivor.EmergencyHelpScreen
import com.example.ui.screens.survivor.FindShelterStep1Screen
import com.example.ui.screens.survivor.FindShelterStep2Screen
import com.example.ui.screens.survivor.FindShelterStep3Screen
import com.example.ui.screens.survivor.HomeScreen
import com.example.ui.screens.survivor.PlacementRequestScreen
import com.example.ui.screens.survivor.RequestStatusScreen
import com.example.ui.screens.survivor.RequestsScreen
import com.example.ui.screens.survivor.ShelterDetailsScreen
import com.example.ui.screens.survivor.ShelterResultsScreen
import com.example.ui.screens.survivor.TransportRequestScreen
import com.example.ui.screens.survivor.TransportStatusScreen
import com.example.ui.screens.survivor.TransportTabScreen
import com.example.ui.screens.transport.TransportDashboardScreen
import com.example.ui.theme.AppGradients
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.SakshamViewModel

object SakshamDestinations {
    const val LOGIN = "login"
    const val ADMIN_LOGIN = "admin_login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val FIND_SHELTER_STEP_1 = "find_shelter_step_1"
    const val FIND_SHELTER_STEP_2 = "find_shelter_step_2"
    const val FIND_SHELTER_STEP_3 = "find_shelter_step_3"
    const val SHELTER_RESULTS = "shelter_results"
    const val SHELTER_DETAILS = "shelter_details"
    const val PLACEMENT_REQUEST = "placement_request"
    const val REQUEST_STATUS = "request_status"
    const val TRANSPORT_REQUEST = "transport_request"
    const val TRANSPORT_STATUS = "transport_status"
    const val REQUESTS = "requests"
    const val TRANSPORT_TAB = "transport_tab"
    const val MORE = "more"
    const val ACCOUNT = "account"
    const val NOTIFICATIONS = "notifications"
    const val PRIVACY = "privacy"
    const val HELP_SUPPORT = "help_support"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val EMERGENCY = "emergency"
}

@Composable
fun SakshamApp(viewModel: SakshamViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentRole by viewModel.currentRole.collectAsState()
    val sessionId by viewModel.sessionId.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userContact by viewModel.userContact.collectAsState()
    val userWheelchairPref by viewModel.userWheelchairPref.collectAsState()
    val userChildPref by viewModel.userChildPref.collectAsState()
    val userTransportPref by viewModel.userTransportPref.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val needs by viewModel.needs.collectAsState()
    val shelters by viewModel.shelters.collectAsState()
    val allSheltersRaw by viewModel.allSheltersRaw.collectAsState()
    val showOnlyEligible by viewModel.showOnlyEligible.collectAsState()
    val selectedShelter by viewModel.selectedShelter.collectAsState()
    val activePlacementRequest by viewModel.activePlacementRequest.collectAsState()
    val activeTransportRequest by viewModel.activeTransportRequest.collectAsState()
    val allPlacementRequests by viewModel.allPlacementRequests.collectAsState()
    val transportProviders by viewModel.transportProviders.collectAsState()
    val allTransportRequests by viewModel.allTransportRequests.collectAsState()
    val availabilityLogs by viewModel.availabilityLogs.collectAsState()
    val staffSelectedShelterId by viewModel.staffSelectedShelterId.collectAsState()

    var showLeaveConfirmationDialog by remember { mutableStateOf(false) }
    var adminReviewShelter by remember { mutableStateOf<Shelter?>(null) }

    val isBookingFlow = currentRoute in listOf(
        SakshamDestinations.FIND_SHELTER_STEP_1,
        SakshamDestinations.FIND_SHELTER_STEP_2,
        SakshamDestinations.FIND_SHELTER_STEP_3,
        SakshamDestinations.SHELTER_RESULTS,
        SakshamDestinations.SHELTER_DETAILS,
        SakshamDestinations.PLACEMENT_REQUEST
    )

    val showBottomBar = currentRole == AppRole.SURVIVOR &&
            currentRoute != null &&
            currentRoute != SakshamDestinations.LOGIN &&
            currentRoute != SakshamDestinations.REGISTER &&
            currentRoute != SakshamDestinations.ADMIN_LOGIN

    if (showLeaveConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmationDialog = false },
            title = {
                Text(
                    text = "Leave this request?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "You are currently applying for shelter placement.\n\nIf you leave now, your current selections will be lost.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirmationDialog = false
                        navController.navigate(SakshamDestinations.HOME) {
                            popUpTo(SakshamDestinations.HOME) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Leave & Go Home", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveConfirmationDialog = false }
                ) {
                    Text("Stay Here", color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradients.LightBackgroundGradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    SakshamBottomNavigation(
                        currentRoute = currentRoute,
                        onNavigateToRoute = { route ->
                            if (route == SakshamDestinations.HOME && isBookingFlow) {
                                showLeaveConfirmationDialog = true
                            } else {
                                navController.navigate(route) {
                                    popUpTo(SakshamDestinations.HOME) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRole) {
                    AppRole.SURVIVOR -> {
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn) SakshamDestinations.HOME else SakshamDestinations.LOGIN,
                            modifier = Modifier.fillMaxSize()
                        ) {
                        // 1. Auth: Login
                        composable(SakshamDestinations.LOGIN) {
                            LoginScreen(
                                onSignInSuccess = { contact, pass ->
                                    viewModel.login(contact, pass)
                                    navController.navigate(SakshamDestinations.HOME) {
                                        popUpTo(SakshamDestinations.LOGIN) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(SakshamDestinations.REGISTER)
                                },
                                onEmergencyClick = {
                                    navController.navigate(SakshamDestinations.EMERGENCY)
                                },
                                onNavigateToAdminLogin = {
                                    navController.navigate(SakshamDestinations.ADMIN_LOGIN)
                                }
                            )
                        }

                        // 1b. Auth: Admin Login
                        composable(SakshamDestinations.ADMIN_LOGIN) {
                            AdminLoginScreen(
                                onAdminLoginSuccess = { contact, pass ->
                                    viewModel.loginAdmin(
                                        contact = contact,
                                        pass = pass,
                                        onSuccess = {},
                                        onError = {}
                                    )
                                },
                                onBackToUserLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 2. Auth: Register
                        composable(SakshamDestinations.REGISTER) {
                            RegisterScreen(
                                onRegisterSuccess = { name, contact, pass ->
                                    viewModel.register(name, contact, pass)
                                    navController.navigate(SakshamDestinations.HOME) {
                                        popUpTo(SakshamDestinations.LOGIN) { inclusive = true }
                                    }
                                },
                                onNavigateToSignIn = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. User Dashboard / Home (Section 3 & 4)
                        composable(SakshamDestinations.HOME) {
                            HomeScreen(
                                userName = userName,
                                unreadNotificationCount = notifications.size,
                                activeRequest = activePlacementRequest,
                                onFindShelterClick = {
                                    viewModel.resetNeeds()
                                    navController.navigate(SakshamDestinations.FIND_SHELTER_STEP_1)
                                },
                                onViewStatusClick = { reqId ->
                                    viewModel.selectPlacementRequest(reqId)
                                    navController.navigate(SakshamDestinations.REQUEST_STATUS)
                                },
                                onShowRequestsClick = {
                                    navController.navigate(SakshamDestinations.REQUESTS)
                                },
                                onNotificationsClick = {
                                    navController.navigate(SakshamDestinations.NOTIFICATIONS)
                                },
                                onTransportActionClick = {
                                    navController.navigate(SakshamDestinations.TRANSPORT_TAB)
                                },
                                onHelpActionClick = {
                                    navController.navigate(SakshamDestinations.HELP_SUPPORT)
                                }
                            )
                        }

                        // 4. Find Shelter Step 1: Who needs shelter? (Section 1)
                        composable(SakshamDestinations.FIND_SHELTER_STEP_1) {
                            FindShelterStep1Screen(
                                adults = needs.adults,
                                children = needs.children,
                                onAdultsChange = { viewModel.updateAdults(it) },
                                onChildrenChange = { viewModel.updateChildren(it) },
                                onNextClick = {
                                    navController.navigate(SakshamDestinations.FIND_SHELTER_STEP_2)
                                },
                                onLeaveFlow = {
                                    navController.navigate(SakshamDestinations.HOME) {
                                        popUpTo(SakshamDestinations.HOME) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 5. Find Shelter Step 2: Additional requirements (Section 2)
                        composable(SakshamDestinations.FIND_SHELTER_STEP_2) {
                            FindShelterStep2Screen(
                                childrenCount = needs.children,
                                wheelchairRequired = needs.wheelchairRequired,
                                childFriendlyRequired = needs.needsChildren,
                                transportRequired = needs.transportRequired,
                                onToggleWheelchair = { viewModel.toggleWheelchairRequirement(it) },
                                onToggleChildFriendly = { viewModel.toggleChildRequirement(it) },
                                onTransportChange = { viewModel.setTransportRequired(it) },
                                onContinueClick = {
                                    navController.navigate(SakshamDestinations.FIND_SHELTER_STEP_3)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 6. Find Shelter Step 3 (redirects to Shelter Results for compatibility)
                        composable(SakshamDestinations.FIND_SHELTER_STEP_3) {
                            FindShelterStep3Screen(
                                adults = needs.adults,
                                children = needs.children,
                                totalPeople = needs.totalPeople,
                                wheelchairRequired = needs.wheelchairRequired,
                                transportRequired = needs.transportRequired,
                                onFindSheltersClick = {
                                    navController.navigate(SakshamDestinations.SHELTER_RESULTS)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 7. Shelter Results (Section 15)
                        composable(SakshamDestinations.SHELTER_RESULTS) {
                            ShelterResultsScreen(
                                needs = needs,
                                shelters = shelters,
                                allSheltersRaw = allSheltersRaw,
                                showOnlyEligible = showOnlyEligible,
                                onToggleShowOnlyEligible = { viewModel.toggleShowOnlyEligible(it) },
                                onSelectShelter = { shelter ->
                                    viewModel.selectShelter(shelter.id)
                                    navController.navigate(SakshamDestinations.SHELTER_DETAILS)
                                },
                                onRequestPlacement = { shelter ->
                                    viewModel.selectShelter(shelter.id)
                                    navController.navigate(SakshamDestinations.PLACEMENT_REQUEST)
                                },
                                onChangeNeedsClick = {
                                    navController.navigate(SakshamDestinations.FIND_SHELTER_STEP_1) {
                                        popUpTo(SakshamDestinations.FIND_SHELTER_STEP_1) { inclusive = true }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 8. Shelter Details (Section 16)
                        composable(SakshamDestinations.SHELTER_DETAILS) {
                            ShelterDetailsScreen(
                                shelter = selectedShelter,
                                needs = needs,
                                onRequestPlacementClick = { shelter ->
                                    viewModel.selectShelter(shelter.id)
                                    navController.navigate(SakshamDestinations.PLACEMENT_REQUEST)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 9. Placement Request (Section 17)
                        composable(SakshamDestinations.PLACEMENT_REQUEST) {
                            PlacementRequestScreen(
                                shelter = selectedShelter,
                                sessionId = sessionId,
                                needs = needs,
                                onConfirmSend = { shelter, transportRequested ->
                                    viewModel.submitPlacementRequest(shelter, transportRequested) { newId ->
                                        viewModel.selectPlacementRequest(newId)
                                        navController.navigate(SakshamDestinations.REQUEST_STATUS) {
                                            popUpTo(SakshamDestinations.HOME)
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 10. Request Status (Section 18 & 19)
                        composable(SakshamDestinations.REQUEST_STATUS) {
                            RequestStatusScreen(
                                request = activePlacementRequest,
                                transportRequest = activeTransportRequest,
                                onRequestTransportClick = {
                                    navController.navigate(SakshamDestinations.TRANSPORT_REQUEST)
                                },
                                onViewTransportStatusClick = {
                                    navController.navigate(SakshamDestinations.TRANSPORT_STATUS)
                                },
                                onSwitchToShelterDashboardClick = {
                                    viewModel.setRole(AppRole.SHELTER_STAFF)
                                },
                                onTryAnotherShelterClick = {
                                    navController.navigate(SakshamDestinations.SHELTER_RESULTS)
                                },
                                onBackClick = {
                                    navController.navigate(SakshamDestinations.HOME) {
                                        popUpTo(SakshamDestinations.HOME) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 11. Transport Request (Section 20)
                        composable(SakshamDestinations.TRANSPORT_REQUEST) {
                            TransportRequestScreen(
                                placementRequest = activePlacementRequest,
                                needs = needs,
                                onSubmitTransportRequest = { placementId, shelterName ->
                                    viewModel.requestTransport(placementId, shelterName) {
                                        navController.navigate(SakshamDestinations.TRANSPORT_STATUS) {
                                            popUpTo(SakshamDestinations.REQUEST_STATUS)
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 12. Transport Status (Section 20)
                        composable(SakshamDestinations.TRANSPORT_STATUS) {
                            TransportStatusScreen(
                                transportRequest = activeTransportRequest,
                                onSwitchToTransportDashboardClick = {
                                    viewModel.setRole(AppRole.TRANSPORT_PROVIDER)
                                },
                                onEmergencyCallClick = {
                                    navController.navigate(SakshamDestinations.EMERGENCY)
                                },
                                onBackClick = {
                                    navController.navigate(SakshamDestinations.REQUEST_STATUS)
                                }
                            )
                        }

                        // 13. Bottom Bar: Requests Tab (Section 7)
                        composable(SakshamDestinations.REQUESTS) {
                            RequestsScreen(
                                requests = allPlacementRequests,
                                transportRequests = allTransportRequests,
                                onViewRequestDetails = { reqId ->
                                    viewModel.selectPlacementRequest(reqId)
                                    navController.navigate(SakshamDestinations.REQUEST_STATUS)
                                },
                                onViewTransportDetails = {
                                    navController.navigate(SakshamDestinations.TRANSPORT_STATUS)
                                },
                                onFindShelterClick = {
                                    navController.navigate(SakshamDestinations.FIND_SHELTER_STEP_1)
                                }
                            )
                        }

                        // 14. Bottom Bar: Transport Tab (Section 8)
                        composable(SakshamDestinations.TRANSPORT_TAB) {
                            TransportTabScreen(
                                activeTransport = activeTransportRequest,
                                activePlacementRequest = activePlacementRequest,
                                onViewTransportDetails = {
                                    navController.navigate(SakshamDestinations.TRANSPORT_STATUS)
                                },
                                onViewMyRequest = { reqId ->
                                    if (reqId != null) {
                                        viewModel.selectPlacementRequest(reqId)
                                    }
                                    navController.navigate(SakshamDestinations.REQUEST_STATUS)
                                }
                            )
                        }

                        // 15. Bottom Bar: More Tab (Section 9)
                        composable(SakshamDestinations.MORE) {
                            MoreScreen(
                                userName = userName,
                                userContact = userContact,
                                unreadNotificationCount = notifications.size,
                                onNavigateToAccount = {
                                    navController.navigate(SakshamDestinations.ACCOUNT)
                                },
                                onNavigateToEmergency = {
                                    navController.navigate(SakshamDestinations.EMERGENCY)
                                },
                                onNavigateToNotifications = {
                                    navController.navigate(SakshamDestinations.NOTIFICATIONS)
                                },
                                onNavigateToHelp = {
                                    navController.navigate(SakshamDestinations.HELP_SUPPORT)
                                },
                                onNavigateToPrivacy = {
                                    navController.navigate(SakshamDestinations.PRIVACY)
                                },
                                onNavigateToSettings = {
                                    navController.navigate(SakshamDestinations.SETTINGS)
                                },
                                onNavigateToAbout = {
                                    navController.navigate(SakshamDestinations.ABOUT)
                                },
                                onLogoutClick = {
                                    viewModel.logout()
                                    navController.navigate(SakshamDestinations.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 16. My Account (Section 10)
                        composable(SakshamDestinations.ACCOUNT) {
                            MyAccountScreen(
                                currentName = userName,
                                currentContact = userContact,
                                initialWheelchairPref = userWheelchairPref,
                                initialChildPref = userChildPref,
                                initialTransportPref = userTransportPref,
                                onSaveProfile = { name, contact, wheelchair, child, transport ->
                                    viewModel.updateProfile(name, contact, wheelchair, child, transport)
                                },
                                onLogoutClick = {
                                    viewModel.logout()
                                    navController.navigate(SakshamDestinations.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 17. Emergency Help (Section 11)
                        composable(SakshamDestinations.EMERGENCY) {
                            EmergencyHelpScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 18. Notifications (Section 21)
                        composable(SakshamDestinations.NOTIFICATIONS) {
                            NotificationsScreen(
                                notifications = notifications,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 19. Privacy Info (Section 22)
                        composable(SakshamDestinations.PRIVACY) {
                            PrivacyInfoScreen(
                                sessionId = sessionId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 20. Help & Support
                        composable(SakshamDestinations.HELP_SUPPORT) {
                            HelpSupportScreen(
                                onEmergencyClick = {
                                    navController.navigate(SakshamDestinations.EMERGENCY)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 21. Settings
                        composable(SakshamDestinations.SETTINGS) {
                            SettingsScreen(
                                currentRole = currentRole,
                                onRoleChange = { newRole ->
                                    viewModel.setRole(newRole)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 22. About Saksham
                        composable(SakshamDestinations.ABOUT) {
                            AboutScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }

                AppRole.SHELTER_STAFF -> {
                    ShelterDashboardScreen(
                        shelters = allSheltersRaw,
                        selectedShelterId = staffSelectedShelterId,
                        placementRequests = allPlacementRequests,
                        availabilityLogs = availabilityLogs,
                        onSelectShelter = { viewModel.setStaffSelectedShelter(it) },
                        onUpdateBeds = { id, beds, name ->
                            viewModel.updateShelterBeds(id, beds, name)
                        },
                        onConfirmRequest = { reqId ->
                            viewModel.confirmPlacement(reqId)
                        },
                        onRejectRequest = { reqId, reason ->
                            viewModel.rejectPlacement(reqId, reason)
                        },
                        onSwitchRoleToSurvivor = {
                            viewModel.setRole(AppRole.SURVIVOR)
                        }
                    )
                }

                AppRole.TRANSPORT_PROVIDER -> {
                    TransportDashboardScreen(
                        transportRequests = allTransportRequests,
                        transportProviders = transportProviders,
                        onAssignVehicle = { tId, pId, provider, eta ->
                            viewModel.assignVehicleToRequest(tId, pId, provider, eta)
                        },
                        onUpdateTransportStatus = { tId, pId, providerId, status ->
                            viewModel.updateTransportStatus(tId, pId, providerId, status)
                        },
                        onSetEta = { tId, eta ->
                            viewModel.setTransportEta(tId, eta)
                        },
                        onToggleVehicleAvailability = { pId, available ->
                            viewModel.toggleVehicleAvailability(pId, available)
                        },
                        onDeclineRequest = { tId, reason ->
                            viewModel.declineTransportRequest(tId, reason)
                        },
                        onSwitchRoleToSurvivor = {
                            viewModel.setRole(AppRole.SURVIVOR)
                        }
                    )
                }

                AppRole.ADMIN -> {
                    val reviewShelter = adminReviewShelter
                    if (reviewShelter != null) {
                        val currentReviewShelter = allSheltersRaw.find { it.id == reviewShelter.id } ?: reviewShelter
                        AdminShelterReviewScreen(
                            shelter = currentReviewShelter,
                            onApproveShelter = { id, callback ->
                                viewModel.approveShelter(id, callback)
                            },
                            onRejectShelter = { id, reason, callback ->
                                viewModel.rejectShelter(id, reason, callback)
                            },
                            onBackClick = {
                                adminReviewShelter = null
                            }
                        )
                    } else {
                        AdminDashboardScreen(
                            shelters = allSheltersRaw,
                            onReviewShelter = { shelter ->
                                adminReviewShelter = shelter
                            },
                            onLogout = {
                                viewModel.logout()
                                navController.navigate(SakshamDestinations.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onSwitchRoleToSurvivor = {
                                viewModel.setRole(AppRole.SURVIVOR)
                            }
                        )
                    }
                }
            }
        }
    }
}
}


