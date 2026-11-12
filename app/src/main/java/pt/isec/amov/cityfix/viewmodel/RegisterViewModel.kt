package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.Validators

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
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
                nifError = Validators.nifError(it.nif)
            )
        }
        val current = _registerState.value
        if (!current.isValid) return

        viewModelScope.launch {
            _registerState.update { it.copy(isSaving = true) }

            val usersList = repository.loadUsers()
            val nif = current.nif.trim()
            if (usersList.hasNif(nif, current.role)) {
                _registerState.update { it.copy(nifError = R.string.error_nif_taken, isSaving = false) }
                return@launch
            }

            val status = if (current.role == UserRole.CITIZEN) AccountStatus.PENDING else AccountStatus.ACTIVE
            usersList.upsert(
                User(
                    name = current.name.trim(),
                    email = current.email.trim(),
                    phone = current.phone.trim(),
                    nif = nif,
                    role = current.role,
                    status = status,
                    createdAt = System.currentTimeMillis(),
                    picturePath = current.picturePath,
                    impactDetectionEnabled = current.impactDetectionEnabled
                )
            )
            repository.saveUsers(usersList)
            _registerState.update { it.copy(isSaving = false, registeredStatus = status) }
        }
    }
}
