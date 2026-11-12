package pt.isec.amov.cityfix.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data class IncidentDetailRoute(val incidentId: String)

@Serializable
data object ReportIncidentRoute

@Serializable
data object CategoryListRoute

@Serializable
data class EditCategoryRoute(val categoryId: String? = null)

@Serializable
data object RegisterRoute

@Serializable
data object CitizenValidationRoute

@Serializable
data object ProfileRoute
