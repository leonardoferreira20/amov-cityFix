package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.DeviceTokenRepository
import pt.isec.amov.cityfix.data.UserRepository

class SessionViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val deviceTokenRepository: DeviceTokenRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SessionViewModel(authRepository, userRepository, categoryRepository, deviceTokenRepository) as T
}
