package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.Validators

class ProfileViewModel(
    private val repository: UserRepository,
    private val userId: String
) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private var savedPicturePath: String? = null

    init {
        viewModelScope.launch {
            repository.loadUsers().findById(userId)?.let { user ->
                savedPicturePath = user.picturePath
                _profileState.value = ProfileUiState(
                    name = user.name,
                    email = user.email,
                    nif = user.nif,
                    role = user.role,
                    location = user.location,
                    phone = user.phone,
                    impactDetectionEnabled = user.impactDetectionEnabled,
                    picturePath = user.picturePath
                )
            }
        }
    }

    fun onPhoneChanged(newPhone: String) {
        _profileState.update { it.copy(phone = newPhone, phoneError = Validators.phoneError(newPhone)) }
    }

    fun onImpactDetectionChanged(enabled: Boolean) {
        _profileState.update { it.copy(impactDetectionEnabled = enabled) }
    }

    fun onPictureChanged(newPath: String?) {
        val oldPath = _profileState.value.picturePath
        if (oldPath != savedPicturePath && oldPath != newPath) FileUtils.deleteFromPath(oldPath)
        _profileState.update { it.copy(picturePath = newPath) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val current = _profileState.value
        if (current.isSaving) return
        if (!current.isValid) {
            onPhoneChanged(current.phone)
            return
        }
        _profileState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val usersList = repository.loadUsers()
            val user = usersList.findById(userId) ?: return@launch
            usersList.upsert(
                user.copy(
                    phone = current.phone.trim(),
                    impactDetectionEnabled = current.impactDetectionEnabled,
                    picturePath = current.picturePath
                )
            )
            repository.saveUsers(usersList)

            if (savedPicturePath != current.picturePath) FileUtils.deleteFromPath(savedPicturePath)
            savedPicturePath = current.picturePath
            onSuccess()
        }
    }

    override fun onCleared() {
        val picturePath = _profileState.value.picturePath
        if (picturePath != savedPicturePath) FileUtils.deleteFromPath(picturePath)
    }
}
