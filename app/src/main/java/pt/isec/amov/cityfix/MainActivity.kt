package pt.isec.amov.cityfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.components.MainTopAppBar
import pt.isec.amov.cityfix.ui.navigation.CategoryListRoute
import pt.isec.amov.cityfix.ui.navigation.CitizenValidationRoute
import pt.isec.amov.cityfix.ui.navigation.DashboardRoute
import pt.isec.amov.cityfix.ui.navigation.EditCategoryRoute
import pt.isec.amov.cityfix.ui.navigation.IncidentDetailRoute
import pt.isec.amov.cityfix.ui.navigation.RegisterRoute
import pt.isec.amov.cityfix.ui.navigation.ReportIncidentRoute
import pt.isec.amov.cityfix.ui.screens.CategoryListScreen
import pt.isec.amov.cityfix.ui.screens.CitizenValidationScreen
import pt.isec.amov.cityfix.ui.screens.EditCategoryScreen
import pt.isec.amov.cityfix.ui.screens.IncidentDetailScreen
import pt.isec.amov.cityfix.ui.screens.MainScreen
import pt.isec.amov.cityfix.ui.screens.MainScreenContent
import pt.isec.amov.cityfix.ui.screens.RegisterScreen
import pt.isec.amov.cityfix.ui.screens.ReportIncidentScreen
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
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
import pt.isec.amov.cityfix.viewmodel.RegisterViewModel
import pt.isec.amov.cityfix.viewmodel.RegisterViewModelFactory
import pt.isec.amov.cityfix.viewmodel.ReportIncidentViewModel
import pt.isec.amov.cityfix.viewmodel.ReportIncidentViewModelFactory
import pt.isec.amov.cityfix.viewmodel.SessionViewModel
import pt.isec.amov.cityfix.viewmodel.SessionViewModelFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CityFixTheme {
                val app = application as CityFixApp
                val incidentsViewModel: IncidentsViewModel =
                    viewModel(factory = IncidentsViewModelFactory(app.incidentRepository))
                val sessionViewModel: SessionViewModel =
                    viewModel(factory = SessionViewModelFactory(app.userRepository))
                val categoriesViewModel: CategoriesViewModel =
                    viewModel(factory = CategoriesViewModelFactory(app.categoryRepository))
                val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val isDashboard = navBackStackEntry?.destination?.hasRoute<DashboardRoute>() == true
                var isExpanded by rememberSaveable { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        MainTopAppBar(
                            titleRes = screenTitle(navBackStackEntry),
                            canNavigateBack = navBackStackEntry != null && !isDashboard,
                            isDashboard = isDashboard,
                            isExpanded = isExpanded,
                            showCategories = isDashboard && canManageCategories(currentUser),
                            showValidation = isDashboard && canValidateCitizens(currentUser),
                            showRegister = isDashboard && currentUser == null,
                            onNavigateBack = { navController.navigateUp() },
                            onToggleExpand = { isExpanded = !isExpanded },
                            onCategoriesClick = { navController.navigate(CategoryListRoute) },
                            onValidationClick = { navController.navigate(CitizenValidationRoute) },
                            onRegisterClick = { navController.navigate(RegisterRoute) }
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
                                sessionViewModel = sessionViewModel,
                                isExpanded = isExpanded,
                                onIncidentClick = { id -> navController.navigate(IncidentDetailRoute(id)) },
                                onCreateIncident = { navController.navigate(ReportIncidentRoute) }
                            )
                        }

                        composable<ReportIncidentRoute> { backStack ->
                            val reportViewModel: ReportIncidentViewModel = viewModel(
                                viewModelStoreOwner = backStack,
                                factory = ReportIncidentViewModelFactory(
                                    app.incidentRepository,
                                    app.categoryRepository,
                                    currentUser
                                )
                            )
                            val reportState by reportViewModel.reportState.collectAsStateWithLifecycle()
                            ReportIncidentScreen(
                                state = reportState,
                                onCategorySelected = reportViewModel::onCategorySelected,
                                onSituationSelected = reportViewModel::onSituationSelected,
                                onDescriptionChange = reportViewModel::onDescriptionChanged,
                                onVisibilityChange = reportViewModel::onVisibilityChanged,
                                onPhotoChange = reportViewModel::onPhotoChanged,
                                onSaveClick = {
                                    reportViewModel.saveIncident {
                                        incidentsViewModel.refreshIncidents()
                                        navController.popBackStack()
                                    }
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
                                onReopen = { justification, photoPath ->
                                    detailViewModel.reopen(justification, photoPath) {
                                        incidentsViewModel.refreshIncidents()
                                    }
                                },
                                onRequestInformation = { text, onResult ->
                                    detailViewModel.requestInformation(text) { error ->
                                        if (error == null) incidentsViewModel.refreshIncidents()
                                        onResult(error)
                                    }
                                },
                                onReplyToInformationRequest = { requestId, text, onResult ->
                                    detailViewModel.replyToInformationRequest(requestId, text) { error ->
                                        if (error == null) incidentsViewModel.refreshIncidents()
                                        onResult(error)
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
                                factory = RegisterViewModelFactory(app.userRepository)
                            )
                            val registerState by registerViewModel.registerState.collectAsStateWithLifecycle()
                            RegisterScreen(
                                state = registerState,
                                onNameChange = registerViewModel::onNameChanged,
                                onEmailChange = registerViewModel::onEmailChanged,
                                onPhoneChange = registerViewModel::onPhoneChanged,
                                onNifChange = registerViewModel::onNifChanged,
                                onRoleChange = registerViewModel::onRoleChanged,
                                onImpactDetectionChange = registerViewModel::onImpactDetectionChanged,
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
            onProfileSelected = {},
            onlyMine = false,
            onOnlyMineChange = {},
            isExpanded = false,
            onIncidentClick = {},
            showCreateIncident = true,
            onCreateIncident = {}
        )
    }
}
