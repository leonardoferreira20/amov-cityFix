package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.MultiFactorResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.SignInResult
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.utils.Validators
import pt.isec.amov.cityfix.utils.authErrorMessage

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private var resolver: MultiFactorResolver? = null

    fun onEmailChanged(email: String) {
        _loginState.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null, error = null) }
    }

    fun onSecondFactorCodeChanged(code: String) {
        _loginState.update { it.copy(secondFactorCode = code.filter(Char::isDigit).take(6), secondFactorError = null) }
    }

    fun signIn(onSignedIn: (User) -> Unit) {
        val current = _loginState.value
        if (current.isLoading) return
        val emailError = Validators.emailError(current.email.trim(), null)
        val passwordError = if (current.password.isEmpty()) R.string.error_password_required else null
        if (emailError != null || passwordError != null) {
            _loginState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _loginState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                when (val result = authRepository.signIn(current.email.trim(), current.password)) {
                    is SignInResult.Success -> finishSignIn(result.userId, onSignedIn)
                    is SignInResult.MfaRequired -> {
                        resolver = result.resolver
                        _loginState.update { it.copy(isLoading = false, isSecondFactorRequired = true) }
                    }
                }
            } catch (e: Exception) {
                _loginState.update { it.copy(isLoading = false, error = authErrorMessage(e)) }
            }
        }
    }

    fun confirmSecondFactor(onSignedIn: (User) -> Unit) {
        val currentResolver = resolver ?: return
        val code = _loginState.value.secondFactorCode
        if (code.length != 6 || _loginState.value.isLoading) {
            _loginState.update { it.copy(secondFactorError = R.string.error_code_invalid) }
            return
        }
        _loginState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val userId = authRepository.resolveSecondFactor(currentResolver, code)
                resolver = null
                _loginState.update { it.copy(isSecondFactorRequired = false, secondFactorCode = "") }
                finishSignIn(userId, onSignedIn)
            } catch (_: Exception) {
                _loginState.update { it.copy(isLoading = false, secondFactorError = R.string.error_code_invalid) }
            }
        }
    }

    fun cancelSecondFactor() {
        resolver = null
        _loginState.update { it.copy(isSecondFactorRequired = false, secondFactorCode = "", secondFactorError = null) }
    }

    fun sendPasswordReset() {
        val email = _loginState.value.email.trim()
        val emailError = Validators.emailError(email, null)
        if (emailError != null) {
            _loginState.update { it.copy(emailError = emailError) }
            return
        }
        viewModelScope.launch {
            try {
                authRepository.sendPasswordReset(email)
                _loginState.update { it.copy(isResetEmailSent = true, error = null) }
            } catch (e: Exception) {
                _loginState.update { it.copy(error = authErrorMessage(e)) }
            }
        }
    }

    fun dismissMessages() {
        _loginState.update { it.copy(isAccountPending = false, isResetEmailSent = false) }
    }

    private suspend fun finishSignIn(userId: String, onSignedIn: (User) -> Unit) {
        val user = userRepository.loadUser(userId)
        when {
            user == null -> {
                authRepository.signOut()
                _loginState.update { it.copy(isLoading = false, error = R.string.error_profile_missing) }
            }
            user.status == AccountStatus.PENDING -> {
                authRepository.signOut()
                _loginState.update { it.copy(isLoading = false, isAccountPending = true) }
            }
            else -> {
                _loginState.update { it.copy(isLoading = false) }
                onSignedIn(user)
            }
        }
    }
}
