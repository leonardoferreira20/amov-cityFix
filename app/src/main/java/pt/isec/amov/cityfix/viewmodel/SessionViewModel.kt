package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.AuthRepository
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.utils.isOperator

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        authRepository.currentUserId?.let { loadSession(it) }
    }

    fun onSignedIn(user: User) {
        _currentUser.value = user
        if (isOperator(user)) viewModelScope.launch { categoryRepository.seedIfEmpty(user.id) }
    }

    fun refreshCurrentUser() {
        val userId = _currentUser.value?.id ?: return
        loadSession(userId)
    }

    fun signOut() {
        authRepository.signOut()
        _currentUser.value = null
    }

    private fun loadSession(userId: String) {
        viewModelScope.launch {
            val user = userRepository.loadUser(userId)
            if (user == null || user.status != AccountStatus.ACTIVE) {
                signOut()
            } else {
                onSignedIn(user)
            }
        }
    }
}
