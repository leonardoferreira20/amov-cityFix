package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes

data class EditCategoryUiState(
    val name: String = "",
    @StringRes val nameError: Int? = null,
    val isSaving: Boolean = false
) {
    val isValid: Boolean
        get() = nameError == null && name.isNotBlank()
}
