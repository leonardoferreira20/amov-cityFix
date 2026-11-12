package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.UserRole

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val nif: String = "",
    val role: UserRole = UserRole.CITIZEN,
    val location: GeoLocation? = null,
    val phone: String = "",
    val impactDetectionEnabled: Boolean = false,
    val picturePath: String? = null,
    @StringRes val phoneError: Int? = null,
    val isSaving: Boolean = false
) {
    val isValid: Boolean
        get() = phoneError == null && phone.isNotBlank()
}
