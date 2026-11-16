package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.UserRole

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val nif: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val role: UserRole = UserRole.CITIZEN,
    val impactDetectionEnabled: Boolean = false,
    val picturePath: String? = null,
    val location: GeoLocation? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val phoneError: Int? = null,
    @StringRes val nifError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val passwordConfirmationError: Int? = null,
    @StringRes val error: Int? = null,
    @StringRes val locationError: Int? = null,
    val isSaving: Boolean = false,
    val registeredStatus: AccountStatus? = null
) {
    val isValid: Boolean
        get() = nameError == null && emailError == null && phoneError == null && nifError == null &&
            passwordError == null && passwordConfirmationError == null && password.isNotEmpty() &&
            name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && nif.isNotBlank() &&
            (role == UserRole.OPERATOR || location != null)
}
