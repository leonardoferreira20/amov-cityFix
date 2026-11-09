package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.UserRole

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val nif: String = "",
    val role: UserRole = UserRole.CITIZEN,
    val impactDetectionEnabled: Boolean = false,
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val phoneError: Int? = null,
    @StringRes val nifError: Int? = null,
    val isSaving: Boolean = false,
    val registeredStatus: AccountStatus? = null
) {
    val isValid: Boolean
        get() = nameError == null && emailError == null && phoneError == null && nifError == null &&
            name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && nif.isNotBlank()
}
