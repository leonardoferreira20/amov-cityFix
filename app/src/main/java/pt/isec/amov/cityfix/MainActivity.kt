package pt.isec.amov.cityfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.navigation.CategoryListRoute
import pt.isec.amov.cityfix.ui.navigation.CitizenValidationRoute
import pt.isec.amov.cityfix.ui.navigation.DevMenuRoute
import pt.isec.amov.cityfix.ui.navigation.EditCategoryRoute
import pt.isec.amov.cityfix.ui.navigation.RegisterRoute
import pt.isec.amov.cityfix.ui.screens.CategoryListScreen
import pt.isec.amov.cityfix.ui.screens.CitizenValidationScreen
import pt.isec.amov.cityfix.ui.screens.DevMenuScreen
import pt.isec.amov.cityfix.ui.screens.EditCategoryScreen
import pt.isec.amov.cityfix.ui.screens.MainScreen
import pt.isec.amov.cityfix.ui.screens.MainScreenContent
import pt.isec.amov.cityfix.ui.screens.RegisterScreen
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.viewmodel.CategoriesViewModel
import pt.isec.amov.cityfix.viewmodel.CategoriesViewModelFactory
import pt.isec.amov.cityfix.viewmodel.CitizenValidationViewModel
import pt.isec.amov.cityfix.viewmodel.CitizenValidationViewModelFactory
import pt.isec.amov.cityfix.viewmodel.EditCategoryViewModel
import pt.isec.amov.cityfix.viewmodel.EditCategoryViewModelFactory
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModel
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModelFactory
import pt.isec.amov.cityfix.viewmodel.RegisterViewModel
import pt.isec.amov.cityfix.viewmodel.RegisterViewModelFactory


// SELEÇÃO DO ECRÃ

enum class Screen {
    JOAO,
    LEONARDO
}

private val selectedScreen = Screen.LEONARDO

private const val SAMPLE_OPERATOR_ID = "operator-1"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CityFixTheme {
                val app = application as CityFixApp
                val incidentsViewModel: IncidentsViewModel =
                    viewModel(factory = IncidentsViewModelFactory(app.incidentRepository))
                when (selectedScreen) {
                    Screen.JOAO -> MainScreen(incidentsViewModel)
                    Screen.LEONARDO -> {
                        val categoriesViewModel: CategoriesViewModel =
                            viewModel(factory = CategoriesViewModelFactory(app.categoryRepository))
                        val navController = rememberNavController()

                        NavHost(navController = navController, startDestination = DevMenuRoute) {
                            composable<DevMenuRoute> {
                                DevMenuScreen(
                                    onCategoriesClick = { navController.navigate(CategoryListRoute) },
                                    onRegisterClick = { navController.navigate(RegisterRoute) },
                                    onValidationClick = { navController.navigate(CitizenValidationRoute) }
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
                                        SAMPLE_OPERATOR_ID
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
}

// =====================================================
// PREVIEW
// =====================================================

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    CityFixTheme {
        when (selectedScreen) {

            Screen.JOAO -> {
                MainScreenContent(
                    incidents = listOf(
                        Incident(
                            id = "1",
                            authorId = "user001",
                            categoryId = "cat001",
                            categoryName = "Telhado",
                            situation = Situation.DAMAGE,
                            description = "O foguete vai no aire... arrebenta fica todo qemado, mas ningem... hic.... quas meninas da ribera do sado!",
                            visibility = Visibility.PUBLIC,
                            location = GeoLocation(40.21, -8.43),
                            status = IncidentStatus.OPEN,
                            media = emptyList(),
                            createdAt = System.currentTimeMillis()
                        ),
                        Incident(
                            id = "2",
                            authorId = "user002",
                            categoryId = "cat002",
                            categoryName = "Sinal Transito",
                            situation = Situation.DESTRUCTION,
                            description = "Fude-se o poste. Nao tive culpa sr guarda",
                            visibility = Visibility.PUBLIC,
                            location = GeoLocation(40.22, -8.44),
                            status = IncidentStatus.OPEN,
                            media = emptyList(),
                            createdAt = System.currentTimeMillis()
                        ),
                        Incident(
                            id = "3",
                            authorId = "user002",
                            categoryId = "cat002",
                            categoryName = "Caralho em pe",
                            situation = Situation.DESTRUCTION,
                            description = "Fude-se o poste. Nao tive culpa sr guarda",
                            visibility = Visibility.PUBLIC,
                            location = GeoLocation(40.22, -8.44),
                            status = IncidentStatus.OPEN,
                            media = emptyList(),
                            createdAt = System.currentTimeMillis()
                        ),
                        Incident(
                            id = "4",
                            authorId = "user002",
                            categoryId = "cat002",
                            categoryName = "Conas",
                            situation = Situation.DESTRUCTION,
                            description = "Fude-se o poste. Nao tive culpa sr guarda",
                            visibility = Visibility.PUBLIC,
                            location = GeoLocation(40.22, -8.44),
                            status = IncidentStatus.OPEN,
                            media = emptyList(),
                            createdAt = System.currentTimeMillis()
                        )
                    ),
                    isExpanded = false,
                    onIncidentClick = {}
                )
            }

            Screen.LEONARDO -> {
                CitizenValidationScreen(
                    citizens = listOf(
                        User(
                            name = "Carla Sousa",
                            email = "carla.sousa@example.com",
                            phone = "913000002",
                            nif = "345678901",
                            role = UserRole.CITIZEN,
                            status = AccountStatus.PENDING,
                            createdAt = 0L,
                            location = GeoLocation(40.2101, -8.4189)
                        ),
                        User(
                            name = "Diogo Ferreira",
                            email = "diogo.ferreira@example.com",
                            phone = "914000003",
                            nif = "456789012",
                            role = UserRole.CITIZEN,
                            status = AccountStatus.PENDING,
                            createdAt = 0L
                        )
                    ),
                    onActivate = {}
                )
            }
        }
    }
}