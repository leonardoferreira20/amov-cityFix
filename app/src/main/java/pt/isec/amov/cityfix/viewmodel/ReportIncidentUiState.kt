package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.data.model.Category
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.Visibility

data class ReportIncidentUiState(
    val categories: List<Category> = emptyList(),
    val categoryId: String? = null,
    val situation: Situation? = null,
    val description: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val photoPath: String? = null,
    @StringRes val categoryError: Int? = null,
    @StringRes val situationError: Int? = null,
    @StringRes val descriptionError: Int? = null,
    @StringRes val photoError: Int? = null,
    val isSaving: Boolean = false
) {
    val isValid: Boolean
        get() = categoryId != null && situation != null && description.isNotBlank() && photoPath != null
}
