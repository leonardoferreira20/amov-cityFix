package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole

class SessionViewModel(private val repository: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun selectProfile(role: UserRole?) {
        viewModelScope.launch {
            _currentUser.value = role?.let { selectedRole ->
                repository.loadUsers().rawList().firstOrNull {
                    it.role == selectedRole && it.status == AccountStatus.ACTIVE
                }
            }
        }
    }
}
