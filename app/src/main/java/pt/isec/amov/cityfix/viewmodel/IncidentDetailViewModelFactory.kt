package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.User

class IncidentDetailViewModelFactory(
    private val incidentRepository: IncidentRepository,
    private val userRepository: UserRepository,
    private val incidentId: String,
    private val currentUser: User?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        IncidentDetailViewModel(incidentRepository, userRepository, incidentId, currentUser) as T
}
