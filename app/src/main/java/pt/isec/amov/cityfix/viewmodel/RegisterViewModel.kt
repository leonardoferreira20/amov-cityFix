package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.Validators
import pt.isec.amov.cityfix.utils.authErrorMessage

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun onNameChanged(newName: String) {
        _registerState.update { it.copy(name = newName, nameError = Validators.nameError(newName)) }
    }

    fun onEmailChanged(newEmail: String) {
        _registerState.update { it.copy(email = newEmail, emailError = Validators.emailError(newEmail, it.role)) }
    }

    fun onPhoneChanged(newPhone: String) {
        _registerState.update { it.copy(phone = newPhone, phoneError = Validators.phoneError(newPhone)) }
    }

    fun onNifChanged(newNif: String) {
        _registerState.update { it.copy(nif = newNif, nifError = Validators.nifError(newNif)) }
    }

    fun onPasswordChanged(newPassword: String) {
        _registerState.update {
            it.copy(
                password = newPassword,
                passwordError = Validators.passwordError(newPassword),
                passwordConfirmationError = if (it.passwordConfirmation.isEmpty()) {
                    null
                } else {
                    Validators.passwordConfirmationError(newPassword, it.passwordConfirmation)
                }
            )
        }
    }

    fun onPasswordConfirmationChanged(confirmation: String) {
        _registerState.update {
            it.copy(
                passwordConfirmation = confirmation,
                passwordConfirmationError = Validators.passwordConfirmationError(it.password, confirmation)
            )
        }
    }

    fun onRoleChanged(newRole: UserRole) {
        _registerState.update {
            it.copy(
                role = newRole,
                emailError = if (it.email.isEmpty()) null else Validators.emailError(it.email, newRole),
                nifError = if (it.nif.isEmpty()) null else Validators.nifError(it.nif),
                impactDetectionEnabled = newRole == UserRole.CITIZEN && it.impactDetectionEnabled
            )
        }
    }

    fun onImpactDetectionChanged(enabled: Boolean) {
        _registerState.update { it.copy(impactDetectionEnabled = enabled) }
    }

    fun onLocationSelected(location: GeoLocation) {
        _registerState.update { it.copy(location = location, locationError = null) }
    }

    fun onPictureChanged(newPath: String?) {
        val oldPath = _registerState.value.picturePath
        if (oldPath != null && oldPath != newPath) FileUtils.deleteFromPath(oldPath)
        _registerState.update { it.copy(picturePath = newPath) }
    }

    override fun onCleared() {
        val state = _registerState.value
        if (state.registeredStatus == null) FileUtils.deleteFromPath(state.picturePath)
    }

    fun register() {
        _registerState.update {
            it.copy(
                nameError = Validators.nameError(it.name),
                emailError = Validators.emailError(it.email, it.role),
                phoneError = Validators.phoneError(it.phone),
                nifError = Validators.nifError(it.nif),
                passwordError = Validators.passwordError(it.password),
                passwordConfirmationError = Validators.passwordConfirmationError(it.password, it.passwordConfirmation),
                locationError = if (it.role == UserRole.CITIZEN && it.location == null) R.string.error_location_required else null
            )
        }
        val current = _registerState.value
        if (!current.isValid || current.isSaving) return

        _registerState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                val userId = authRepository.register(current.email.trim(), current.password)
                val nif = current.nif.trim()
                if (userRepository.hasNif(nif, current.role)) {
                    authRepository.deleteCurrentUser()
                    _registerState.update { it.copy(nifError = R.string.error_nif_taken, isSaving = false) }
                    return@launch
                }
                val status = if (current.role == UserRole.CITIZEN) AccountStatus.PENDING else AccountStatus.ACTIVE
                userRepository.createUser(
                    User(
                        id = userId,
                        name = current.name.trim(),
                        email = current.email.trim(),
                        phone = current.phone.trim(),
                        nif = nif,
                        role = current.role,
                        status = status,
                        createdAt = System.currentTimeMillis(),
                        picturePath = current.picturePath,
                        location = if (current.role == UserRole.CITIZEN) current.location else null,
                        impactDetectionEnabled = current.impactDetectionEnabled
                    )
                )
                authRepository.signOut()
                _registerState.update { it.copy(isSaving = false, registeredStatus = status) }
            } catch (e: Exception) {
                if (authRepository.currentUserId != null) authRepository.deleteCurrentUser()
                val message = authErrorMessage(e)
                _registerState.update {
                    when (message) {
                        R.string.error_email_taken -> it.copy(isSaving = false, emailError = message)
                        R.string.error_password_weak -> it.copy(isSaving = false, passwordError = message)
                        else -> it.copy(isSaving = false, error = message)
                    }
                }
            }
        }
    }
}
