package pt.isec.amov.cityfix.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DevMenuRoute

@Serializable
data object CategoryListRoute

@Serializable
data class EditCategoryRoute(val categoryId: String? = null)

@Serializable
data object RegisterRoute

@Serializable
data object CitizenValidationRoute
