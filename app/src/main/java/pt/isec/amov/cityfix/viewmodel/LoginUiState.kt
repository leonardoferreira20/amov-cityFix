package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val error: Int? = null,
    val isLoading: Boolean = false,
    val isSecondFactorRequired: Boolean = false,
    val secondFactorCode: String = "",
    @StringRes val secondFactorError: Int? = null,
    val isAccountPending: Boolean = false,
    val isResetEmailSent: Boolean = false
)
