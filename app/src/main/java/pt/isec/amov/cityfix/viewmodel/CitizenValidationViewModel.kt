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
import pt.isec.amov.cityfix.data.model.UsersList

class CitizenValidationViewModel(private val repository: UserRepository) : ViewModel() {
    private val _pendingCitizens = MutableStateFlow<List<User>>(emptyList())
    val pendingCitizens: StateFlow<List<User>> = _pendingCitizens.asStateFlow()
    private var currentUsersList = UsersList()

    init {
        viewModelScope.launch {
            currentUsersList = repository.loadUsers()
            _pendingCitizens.value = currentUsersList.getPendingCitizensSorted()
        }
    }

    fun activateCitizen(userId: String) {
        val user = currentUsersList.findById(userId) ?: return
        viewModelScope.launch {
            currentUsersList.upsert(user.copy(status = AccountStatus.ACTIVE))
            repository.saveUsers(currentUsersList)
            _pendingCitizens.value = currentUsersList.getPendingCitizensSorted()
        }
    }
}
