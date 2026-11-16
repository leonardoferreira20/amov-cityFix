package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.TotpSecret
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.Validators
import pt.isec.amov.cityfix.utils.authErrorMessage

private const val TOTP_ISSUER = "CityFix"

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
    private val userId: String
) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private var savedPicturePath: String? = null
    private var totpSecret: TotpSecret? = null

    init {
        viewModelScope.launch {
            repository.loadUser(userId)?.let { user ->
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
            refreshSecondFactor()
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            try {
                authRepository.sendEmailVerification()
                _profileState.update { it.copy(isVerificationEmailSent = true, secondFactorError = null) }
            } catch (e: Exception) {
                _profileState.update { it.copy(secondFactorError = authErrorMessage(e)) }
            }
        }
    }

    fun checkEmailVerified() {
        viewModelScope.launch { refreshSecondFactor() }
    }

    fun startSecondFactorEnrollment() {
        if (_profileState.value.isSecondFactorLoading) return
        _profileState.update { it.copy(isSecondFactorLoading = true, secondFactorError = null) }
        viewModelScope.launch {
            try {
                val secret = authRepository.startTotpEnrollment()
                totpSecret = secret
                _profileState.update {
                    it.copy(
                        isSecondFactorLoading = false,
                        totpSecretKey = secret.sharedSecretKey,
                        totpUri = secret.generateQrCodeUrl(it.email, TOTP_ISSUER)
                    )
                }
            } catch (e: Exception) {
                _profileState.update { it.copy(isSecondFactorLoading = false, secondFactorError = authErrorMessage(e)) }
            }
        }
    }

    fun onTotpCodeChanged(code: String) {
        _profileState.update { it.copy(totpCode = code.filter(Char::isDigit).take(6), secondFactorError = null) }
    }

    fun confirmSecondFactorEnrollment() {
        val secret = totpSecret ?: return
        val code = _profileState.value.totpCode
        if (code.length != 6) {
            _profileState.update { it.copy(secondFactorError = R.string.error_code_invalid) }
            return
        }
        _profileState.update { it.copy(isSecondFactorLoading = true) }
        viewModelScope.launch {
            try {
                authRepository.finishTotpEnrollment(secret, code)
                totpSecret = null
                _profileState.update {
                    it.copy(
                        isSecondFactorLoading = false,
                        hasSecondFactor = true,
                        totpSecretKey = null,
                        totpUri = null,
                        totpCode = ""
                    )
                }
            } catch (_: Exception) {
                _profileState.update { it.copy(isSecondFactorLoading = false, secondFactorError = R.string.error_code_invalid) }
            }
        }
    }

    fun cancelSecondFactorEnrollment() {
        totpSecret = null
        _profileState.update { it.copy(totpSecretKey = null, totpUri = null, totpCode = "", secondFactorError = null) }
    }

    private suspend fun refreshSecondFactor() {
        try {
            authRepository.reloadUser()
        } catch (_: Exception) {
        }
        _profileState.update {
            it.copy(
                isEmailVerified = authRepository.isEmailVerified,
                hasSecondFactor = authRepository.hasSecondFactor
            )
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
            val user = repository.loadUser(userId)
            if (user == null) {
                _profileState.update { it.copy(isSaving = false) }
                return@launch
            }
            repository.saveUser(
                user.copy(
                    phone = current.phone.trim(),
                    impactDetectionEnabled = current.impactDetectionEnabled,
                    picturePath = current.picturePath
                )
            )

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
