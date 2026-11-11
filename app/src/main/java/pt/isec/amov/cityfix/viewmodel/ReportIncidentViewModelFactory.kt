package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amov.cityfix.data.CategoryRepository
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.model.User

class ReportIncidentViewModelFactory(
    private val incidentRepository: IncidentRepository,
    private val categoryRepository: CategoryRepository,
    private val currentUser: User?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ReportIncidentViewModel(incidentRepository, categoryRepository, currentUser) as T
}
