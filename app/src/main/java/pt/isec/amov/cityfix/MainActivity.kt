package pt.isec.amov.cityfix

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.Visibility
import androidx.core.content.ContextCompat
import pt.isec.amov.cityfix.services.ImpactDetectionService
import pt.isec.amov.cityfix.services.ProximityService
import pt.isec.amov.cityfix.ui.components.BackgroundLocationDialog
import pt.isec.amov.cityfix.ui.components.LifecycleActions
import pt.isec.amov.cityfix.ui.components.MainTopAppBar
import pt.isec.amov.cityfix.ui.components.rememberDeviceLocation
import pt.isec.amov.cityfix.ui.navigation.CategoryListRoute
import pt.isec.amov.cityfix.ui.navigation.CitizenValidationRoute
import pt.isec.amov.cityfix.ui.navigation.DashboardRoute
import pt.isec.amov.cityfix.ui.navigation.EditCategoryRoute
import pt.isec.amov.cityfix.ui.navigation.IncidentDetailRoute
import pt.isec.amov.cityfix.ui.navigation.IncidentsMapRoute
import pt.isec.amov.cityfix.ui.navigation.LoginRoute
import pt.isec.amov.cityfix.ui.navigation.ProfileRoute
import pt.isec.amov.cityfix.ui.navigation.RegisterRoute
import pt.isec.amov.cityfix.ui.navigation.ReportIncidentRoute
import pt.isec.amov.cityfix.ui.screens.CategoryListScreen
import pt.isec.amov.cityfix.ui.screens.CitizenValidationScreen
import pt.isec.amov.cityfix.ui.screens.EditCategoryScreen
import pt.isec.amov.cityfix.ui.screens.IncidentDetailScreen
import pt.isec.amov.cityfix.ui.screens.IncidentsMapScreen
import pt.isec.amov.cityfix.ui.screens.LoginScreen
import pt.isec.amov.cityfix.ui.screens.MainScreen
import pt.isec.amov.cityfix.ui.screens.MainScreenContent
import pt.isec.amov.cityfix.ui.screens.ProfileScreen
import pt.isec.amov.cityfix.ui.screens.RegisterScreen
import pt.isec.amov.cityfix.ui.screens.ReportIncidentScreen
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.utils.AnonymousAccess
import pt.isec.amov.cityfix.utils.IncidentNotifications
import pt.isec.amov.cityfix.utils.LocationUtils
import pt.isec.amov.cityfix.utils.anonymousAccess
import pt.isec.amov.cityfix.utils.canEditProfile
import pt.isec.amov.cityfix.utils.canSeeExactLocation
import pt.isec.amov.cityfix.utils.isOperator
import pt.isec.amov.cityfix.utils.canCreateIncident
import pt.isec.amov.cityfix.utils.needsBackgroundLocation
import pt.isec.amov.cityfix.utils.runsImpactDetection
import pt.isec.amov.cityfix.utils.runsProximityService
import pt.isec.amov.cityfix.utils.canManageCategories
import pt.isec.amov.cityfix.utils.canValidateCitizens
import pt.isec.amov.cityfix.viewmodel.CategoriesViewModel
import pt.isec.amov.cityfix.viewmodel.CategoriesViewModelFactory
import pt.isec.amov.cityfix.viewmodel.CitizenValidationViewModel
import pt.isec.amov.cityfix.viewmodel.CitizenValidationViewModelFactory
import pt.isec.amov.cityfix.viewmodel.EditCategoryViewModel
import pt.isec.amov.cityfix.viewmodel.EditCategoryViewModelFactory
import pt.isec.amov.cityfix.viewmodel.IncidentDetailViewModel
import pt.isec.amov.cityfix.viewmodel.IncidentDetailViewModelFactory
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModel
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModelFactory
import pt.isec.amov.cityfix.viewmodel.LoginViewModel
import pt.isec.amov.cityfix.viewmodel.LoginViewModelFactory
import pt.isec.amov.cityfix.viewmodel.ProfileViewModel
import pt.isec.amov.cityfix.viewmodel.ProfileViewModelFactory
import pt.isec.amov.cityfix.viewmodel.RegisterViewModel
import pt.isec.amov.cityfix.viewmodel.RegisterViewModelFactory
import pt.isec.amov.cityfix.viewmodel.ReportIncidentViewModel
import pt.isec.amov.cityfix.viewmodel.ReportIncidentViewModelFactory
import pt.isec.amov.cityfix.viewmodel.SessionViewModel
import pt.isec.amov.cityfix.viewmodel.SettingsViewModel
import pt.isec.amov.cityfix.viewmodel.SettingsViewModelFactory
import pt.isec.amov.cityfix.viewmodel.SessionViewModelFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val openedIncidentId = intent.getStringExtra(IncidentNotifications.EXTRA_INCIDENT_ID)
        val openedImpact = if (intent.hasExtra(IncidentNotifications.EXTRA_IMPACT_LATITUDE)) {
            intent.getDoubleExtra(IncidentNotifications.EXTRA_IMPACT_LATITUDE, 0.0) to
                intent.getDoubleExtra(IncidentNotifications.EXTRA_IMPACT_LONGITUDE, 0.0)
        } else {
            null
        }

        setContent {
            val app = application as CityFixApp
            val settingsViewModel: SettingsViewModel =
                viewModel(factory = SettingsViewModelFactory(app.settingsRepository))
            val isLargeText by settingsViewModel.isLargeText.collectAsStateWithLifecycle()
            CityFixTheme(largeText = isLargeText) {
                val incidentsViewModel: IncidentsViewModel =
                    viewModel(factory = IncidentsViewModelFactory(app.incidentRepository))
                val sessionViewModel: SessionViewModel =
                    viewModel(
                        factory = SessionViewModelFactory(
                            app.authRepository,
                            app.userRepository,
                            app.categoryRepository,
                            app.deviceTokenRepository
                        )
                    )
                val categoriesViewModel: CategoriesViewModel =
                    viewModel(factory = CategoriesViewModelFactory(app.categoryRepository))
                val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()
                val incidents by incidentsViewModel.incidentsList.collectAsStateWithLifecycle()

                val context = LocalContext.current
                var hasLocationPermission by remember {
                    mutableStateOf(LocationUtils.hasLocationPermission(context))
                }
                val locationPermissionRequest = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    hasLocationPermission = LocationUtils.hasLocationPermission(context)
                }
                val requestLocationPermission = {
                    val permissions = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    locationPermissionRequest.launch(permissions.toTypedArray())
                }
                LaunchedEffect(Unit) {
                    val hasNotificationPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED
                    if (!hasLocationPermission || !hasNotificationPermission) requestLocationPermission()
                }
                var isLocationEnabled by remember { mutableStateOf(LocationUtils.isLocationEnabled(context)) }
                val locationSettingsRequest = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult()
                ) {
                    isLocationEnabled = LocationUtils.isLocationEnabled(context)
                }
                val requestLocationServices = {
                    LocationUtils.checkLocationSettings(
                        context,
                        onEnabled = { isLocationEnabled = true },
                        onResolutionRequired = locationSettingsRequest::launch
                    )
                }
                val requestLocation = {
                    if (hasLocationPermission) requestLocationServices() else requestLocationPermission()
                }
                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    hasLocationPermission = LocationUtils.hasLocationPermission(context)
                    isLocationEnabled = LocationUtils.isLocationEnabled(context)
                }
                val deviceLocation by rememberDeviceLocation(hasLocationPermission && isLocationEnabled)
                val access = anonymousAccess(currentUser, hasLocationPermission, isLocationEnabled, deviceLocation)

                LaunchedEffect(currentUser?.id, hasLocationPermission) {
                    val user = currentUser
                    if (user != null && runsProximityService(user) && hasLocationPermission) {
                        ContextCompat.startForegroundService(context, ProximityService.intent(context, user.id))
                    } else {
                        context.stopService(Intent(context, ProximityService::class.java))
                    }
                }

                LaunchedEffect(currentUser?.id, currentUser?.impactDetectionEnabled, hasLocationPermission) {
                    val serviceIntent = Intent(context, ImpactDetectionService::class.java)
                    if (runsImpactDetection(currentUser) && hasLocationPermission) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        context.stopService(serviceIntent)
                    }
                }

                var hasBackgroundLocation by remember {
                    mutableStateOf(LocationUtils.hasBackgroundLocationPermission(context))
                }
                var backgroundLocationAsked by rememberSaveable { mutableStateOf(false) }
                val backgroundLocationRequest = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {
                    hasBackgroundLocation = LocationUtils.hasBackgroundLocationPermission(context)
                }
                if (needsBackgroundLocation(currentUser) && hasLocationPermission &&
                    !hasBackgroundLocation && !backgroundLocationAsked &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ) {
                    BackgroundLocationDialog(
                        isOperator = isOperator(currentUser),
                        onAllow = {
                            backgroundLocationAsked = true
                            backgroundLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        },
                        onDismiss = { backgroundLocationAsked = true }
                    )
                }

                val navController = rememberNavController()
                var notificationIncidentId by rememberSaveable { mutableStateOf(openedIncidentId) }
                LaunchedEffect(notificationIncidentId, currentUser) {
                    val incidentId = notificationIncidentId
                    if (incidentId != null && currentUser != null) {
                        navController.navigate(IncidentDetailRoute(incidentId))
                        notificationIncidentId = null
                    }
                }
                var impactSuggestion by rememberSaveable { mutableStateOf(openedImpact) }
                LaunchedEffect(impactSuggestion, currentUser) {
                    val impact = impactSuggestion
                    if (impact != null && canCreateIncident(currentUser)) {
                        navController.navigate(ReportIncidentRoute(impact.first, impact.second))
                        impactSuggestion = null
                    }
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val isDashboard = navBackStackEntry?.destination?.hasRoute<DashboardRoute>() == true
                val locationNeededFor = when {
                    navBackStackEntry?.destination?.hasRoute<RegisterRoute>() == true -> "register"
                    currentUser == null -> "anonymous"
                    runsProximityService(currentUser) -> "proximity"
                    runsImpactDetection(currentUser) -> "impacts"
                    else -> null
                }
                LaunchedEffect(hasLocationPermission, locationNeededFor) {
                    if (hasLocationPermission && locationNeededFor != null && !isLocationEnabled) {
                        requestLocationServices()
                    }
                }
                var expandedIncidentIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
                val areAllIncidentsExpanded = incidents.isNotEmpty() &&
                        incidents.all { it.id in expandedIncidentIds }
                val signOut: () -> Unit = {
                    expandedIncidentIds = emptyList()
                    incidentsViewModel.setCurrentUser(null)
                    sessionViewModel.signOut()
                    navController.popBackStack(DashboardRoute, inclusive = false)
                }

                Scaffold(
                    topBar = {
                        MainTopAppBar(
                            isLargeText = isLargeText,
                            onToggleLargeText = settingsViewModel::toggleLargeText,
                            titleRes = screenTitle(navBackStackEntry),
                            canNavigateBack = navBackStackEntry != null && !isDashboard,
                            showExpandCards = isDashboard && access == AnonymousAccess.ALLOWED && incidents.isNotEmpty(),
                            areAllCardsExpanded = areAllIncidentsExpanded,
                            showMap = isDashboard && access == AnonymousAccess.ALLOWED,
                            showCategories = isDashboard && canManageCategories(currentUser),
                            showValidation = isDashboard && canValidateCitizens(currentUser),
                            showLogin = isDashboard && currentUser == null,
                            showProfile = isDashboard && canEditProfile(currentUser),
                            onNavigateBack = { navController.navigateUp() },
                            onToggleAllCards = {
                                expandedIncidentIds = if (areAllIncidentsExpanded) {
                                    emptyList()
                                } else {
                                    incidents.map { it.id }
                                }
                            },
                            onMapClick = { navController.navigate(IncidentsMapRoute) },
                            onCategoriesClick = { navController.navigate(CategoryListRoute) },
                            onValidationClick = { navController.navigate(CitizenValidationRoute) },
                            onLoginClick = { navController.navigate(LoginRoute) },
                            onProfileClick = { navController.navigate(ProfileRoute) }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = DashboardRoute,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    ) {
                        composable<DashboardRoute> {
                            MainScreen(
                                viewModel = incidentsViewModel,
                                currentUser = currentUser,
                                incidents = incidents,
                                onLoginClick = { navController.navigate(LoginRoute) },
                                onSignOut = signOut,
                                anonymousAccess = access,
                                onRequestLocationPermission = requestLocation,
                                expandedIncidentIds = expandedIncidentIds,
                                onToggleIncident = { id ->
                                    expandedIncidentIds = if (id in expandedIncidentIds) {
                                        expandedIncidentIds - id
                                    } else {
                                        expandedIncidentIds + id
                                    }
                                },
                                onOpenIncident = { id -> navController.navigate(IncidentDetailRoute(id)) },
                                onCreateIncident = { navController.navigate(ReportIncidentRoute()) }
                            )
                        }

                        composable<LoginRoute> { backStack ->
                            val loginViewModel: LoginViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = LoginViewModelFactory(app.authRepository, app.userRepository)
                            )
                            val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
                            val onSignedIn: (User) -> Unit = { user ->
                                incidentsViewModel.setCurrentUser(user)
                                sessionViewModel.onSignedIn(user)
                                navController.popBackStack(DashboardRoute, inclusive = false)
                            }
                            LoginScreen(
                                state = loginState,
                                onEmailChange = loginViewModel::onEmailChanged,
                                onPasswordChange = loginViewModel::onPasswordChanged,
                                onSignInClick = { loginViewModel.signIn(onSignedIn) },
                                onForgotPasswordClick = loginViewModel::sendPasswordReset,
                                onRegisterClick = { navController.navigate(RegisterRoute) },
                                onSecondFactorCodeChange = loginViewModel::onSecondFactorCodeChanged,
                                onConfirmSecondFactor = { loginViewModel.confirmSecondFactor(onSignedIn) },
                                onCancelSecondFactor = loginViewModel::cancelSecondFactor,
                                onDismissMessage = loginViewModel::dismissMessages
                            )
                        }

                        composable<IncidentsMapRoute> {
                            IncidentsMapScreen(
                                incidents = incidents,
                                exact = incidents.isNotEmpty() && incidents.all { canSeeExactLocation(currentUser, it) },
                                onOpenIncident = { id -> navController.navigate(IncidentDetailRoute(id)) }
                            )
                        }

                        composable<ReportIncidentRoute> { backStack ->
                            val route = backStack.toRoute<ReportIncidentRoute>()
                            val impactLocation = if (route.impactLatitude != null && route.impactLongitude != null) {
                                GeoLocation(route.impactLatitude, route.impactLongitude)
                            } else {
                                null
                            }
                            val reportViewModel: ReportIncidentViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = ReportIncidentViewModelFactory(
                                    app.incidentRepository,
                                    app.categoryRepository,
                                    currentUser,
                                    impactLocation
                                )
                            )
                            val reportState by reportViewModel.reportState.collectAsStateWithLifecycle()
                            ReportIncidentScreen(
                                state = reportState,
                                onLocationSelected = reportViewModel::onLocationSelected,
                                onCategorySelected = reportViewModel::onCategorySelected,
                                onSituationSelected = reportViewModel::onSituationSelected,
                                onDescriptionChange = reportViewModel::onDescriptionChanged,
                                onVisibilityChange = reportViewModel::onVisibilityChanged,
                                onPhotoChange = reportViewModel::onPhotoChanged,
                                onVideoChange = reportViewModel::onVideoChanged,
                                onSaveClick = {
                                    reportViewModel.saveIncident { navController.popBackStack() }
                                }
                            )
                        }

                        composable<IncidentDetailRoute> { backStack ->
                            val route = backStack.toRoute<IncidentDetailRoute>()
                            val detailViewModel: IncidentDetailViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = IncidentDetailViewModelFactory(
                                    app.incidentRepository,
                                    app.userRepository,
                                    route.incidentId,
                                    currentUser
                                )
                            )
                            val detailState by detailViewModel.detailState.collectAsStateWithLifecycle()
                            IncidentDetailScreen(
                                state = detailState,
                                deviceLocation = deviceLocation,
                                onReopen = { justification, photoPath ->
                                    detailViewModel.reopen(justification, photoPath) {}
                                },
                                onRequestInformation = { text, onResult ->
                                    detailViewModel.requestInformation(text, onResult)
                                },
                                onReplyToInformationRequest = { requestId, text, onResult ->
                                    detailViewModel.replyToInformationRequest(requestId, text, onResult)
                                },
                                operatorActions = {
                                    detailState.incident?.let { incident ->
                                        LifecycleActions(
                                            status = incident.status,
                                            closesAt = detailState.reopenDeadline,
                                            canStartWork = detailState.canStartWork,
                                            canResolve = detailState.canResolve,
                                            onStartWork = {
                                                detailViewModel.startWork {}
                                            },
                                            onResolve = { photoPath, videoPath, note ->
                                                detailViewModel.resolve(photoPath, videoPath, note) {}
                                            }
                                        )
                                    }
                                }
                            )
                        }

                        composable<CategoryListRoute> {
                            val categories by categoriesViewModel.categoriesList.collectAsStateWithLifecycle()
                            CategoryListScreen(
                                categories = categories,
                                onCategoryClick = { id -> navController.navigate(EditCategoryRoute(id)) },
                                onActiveChange = categoriesViewModel::setCategoryActive,
                                onAddClick = { navController.navigate(EditCategoryRoute()) }
                            )
                        }

                        composable<EditCategoryRoute> { backStack ->
                            val route = backStack.toRoute<EditCategoryRoute>()
                            val editViewModel: EditCategoryViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = EditCategoryViewModelFactory(
                                    app.categoryRepository,
                                    route.categoryId,
                                    currentUser?.id.orEmpty()
                                )
                            )
                            val editState by editViewModel.editState.collectAsStateWithLifecycle()
                            EditCategoryScreen(
                                state = editState,
                                onNameChange = editViewModel::onNameChanged,
                                onSaveClick = {
                                    editViewModel.saveCategory {
                                        categoriesViewModel.refreshCategories()
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        composable<RegisterRoute> { backStack ->
                            val registerViewModel: RegisterViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = RegisterViewModelFactory(app.authRepository, app.userRepository)
                            )
                            val registerState by registerViewModel.registerState.collectAsStateWithLifecycle()
                            RegisterScreen(
                                state = registerState,
                                onNameChange = registerViewModel::onNameChanged,
                                onEmailChange = registerViewModel::onEmailChanged,
                                onPhoneChange = registerViewModel::onPhoneChanged,
                                onNifChange = registerViewModel::onNifChanged,
                                onPasswordChange = registerViewModel::onPasswordChanged,
                                onPasswordConfirmationChange = registerViewModel::onPasswordConfirmationChanged,
                                onRoleChange = registerViewModel::onRoleChanged,
                                onImpactDetectionChange = registerViewModel::onImpactDetectionChanged,
                                onPictureChange = registerViewModel::onPictureChanged,
                                deviceLocation = deviceLocation,
                                onLocationSelected = registerViewModel::onLocationSelected,
                                onRegisterClick = registerViewModel::register,
                                onDone = { navController.popBackStack() }
                            )
                        }

                        composable<CitizenValidationRoute> { backStack ->
                            val validationViewModel: CitizenValidationViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = CitizenValidationViewModelFactory(app.userRepository)
                            )
                            val pendingCitizens by validationViewModel.pendingCitizens.collectAsStateWithLifecycle()
                            CitizenValidationScreen(
                                citizens = pendingCitizens,
                                onActivate = validationViewModel::activateCitizen
                            )
                        }

                        composable<ProfileRoute> { backStack ->
                            val profileViewModel: ProfileViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = ProfileViewModelFactory(app.authRepository, app.userRepository, currentUser?.id.orEmpty())
                            )
                            val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
                            ProfileScreen(
                                state = profileState,
                                onPhoneChange = profileViewModel::onPhoneChanged,
                                onImpactDetectionChange = profileViewModel::onImpactDetectionChanged,
                                onPictureChange = profileViewModel::onPictureChanged,
                                onSaveClick = {
                                    profileViewModel.saveProfile {
                                        sessionViewModel.refreshCurrentUser()
                                        navController.popBackStack()
                                    }
                                },
                                onSendVerificationEmail = profileViewModel::sendVerificationEmail,
                                onCheckEmailVerified = profileViewModel::checkEmailVerified,
                                onStartSecondFactor = profileViewModel::startSecondFactorEnrollment,
                                onTotpCodeChange = profileViewModel::onTotpCodeChanged,
                                onConfirmSecondFactor = profileViewModel::confirmSecondFactorEnrollment,
                                onCancelSecondFactor = profileViewModel::cancelSecondFactorEnrollment,
                                onSignOut = signOut
                            )
                        }
                    }
                }
            }
        }
    }
}

@StringRes
private fun screenTitle(entry: NavBackStackEntry?): Int {
    val destination = entry?.destination
    return when {
        destination?.hasRoute<IncidentDetailRoute>() == true -> R.string.incident_detail_title
        destination?.hasRoute<ReportIncidentRoute>() == true -> R.string.report_title
        destination?.hasRoute<CategoryListRoute>() == true -> R.string.categories_title
        destination?.hasRoute<EditCategoryRoute>() == true ->
            if (entry.toRoute<EditCategoryRoute>().categoryId == null) R.string.category_add
            else R.string.category_edit
        destination?.hasRoute<RegisterRoute>() == true -> R.string.register_title
        destination?.hasRoute<CitizenValidationRoute>() == true -> R.string.validation_title
        destination?.hasRoute<ProfileRoute>() == true -> R.string.profile_title
        destination?.hasRoute<IncidentsMapRoute>() == true -> R.string.map_title
        destination?.hasRoute<LoginRoute>() == true -> R.string.login_title
        else -> R.string.app_name
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    CityFixTheme {
        MainScreenContent(
            stats = DashboardStats(
                averageRepairTimeMillis = TimeUnit.DAYS.toMillis(10),
                totalIncidents = 4,
                activePercentage = 75.0
            ),
            incidents = listOf(
                Incident(
                    id = "1",
                    authorId = "citizen-1",
                    categoryId = "cat-roads",
                    categoryName = "Vias Públicas",
                    situation = Situation.DAMAGE,
                    description = "Buraco grande na faixa da direita.",
                    visibility = Visibility.PUBLIC,
                    location = GeoLocation(40.2086, -8.4207),
                    status = IncidentStatus.OPEN,
                    createdAt = 0L
                ),
                Incident(
                    id = "2",
                    authorId = "citizen-2",
                    categoryId = "cat-lighting",
                    categoryName = "Iluminação",
                    situation = Situation.MALFUNCTION,
                    description = "Candeeiro apagado há uma semana.",
                    visibility = Visibility.PUBLIC,
                    location = GeoLocation(40.2101, -8.4210),
                    status = IncidentStatus.IN_PROGRESS,
                    createdAt = 0L
                ),
                Incident(
                    id = "3",
                    authorId = "citizen-2",
                    categoryId = "cat-signs",
                    categoryName = "Sinalização",
                    situation = Situation.DESTRUCTION,
                    description = "Sinal de STOP derrubado.",
                    visibility = Visibility.PUBLIC,
                    location = GeoLocation(40.2120, -8.4250),
                    status = IncidentStatus.OPEN,
                    createdAt = 0L
                )
            ),
            currentRole = null,
            anonymousAccess = AnonymousAccess.ALLOWED,
            onRequestLocationPermission = {},
            onLoginClick = {},
            onlyMine = false,
            onOnlyMineChange = {},
            expandedIncidentIds = listOf("1"),
            onToggleIncident = {},
            onOpenIncident = {},
            showCreateIncident = false,
            onCreateIncident = {}
        )
    }
}
