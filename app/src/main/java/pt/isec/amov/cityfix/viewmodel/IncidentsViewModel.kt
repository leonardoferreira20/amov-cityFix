package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentsList
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.utils.canViewIncident
import pt.isec.amov.cityfix.utils.isAuthor
import pt.isec.amov.cityfix.utils.isCitizen

class IncidentsViewModel(private val repository: IncidentRepository) : ViewModel() {
    private val _incidentsList = MutableStateFlow<List<Incident>>(emptyList())
    val incidentsList: StateFlow<List<Incident>> = _incidentsList.asStateFlow()
    private val _dashboardStats = MutableStateFlow(DashboardStats(null, 0, 0.0))
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()
    private val _onlyMine = MutableStateFlow(false)
    val onlyMine: StateFlow<Boolean> = _onlyMine.asStateFlow()
    private var currentIncidentsList = IncidentsList()
    private var currentUser: User? = null

    init {
        refreshIncidents()
    }

    fun refreshIncidents() {
        viewModelScope.launch {
            currentIncidentsList = repository.loadIncidents()
            _dashboardStats.value = currentIncidentsList.getDashboardStats()
            updateVisibleIncidents()
        }
    }

    fun setCurrentUser(user: User?) {
        currentUser = user
        if (!isCitizen(user)) _onlyMine.value = false
        updateVisibleIncidents()
    }

    fun setOnlyMine(onlyMine: Boolean) {
        _onlyMine.value = onlyMine
        updateVisibleIncidents()
    }

    fun getIncidentById(id: String): Incident? = currentIncidentsList.findById(id)

    private fun updateVisibleIncidents() {
        _incidentsList.value = currentIncidentsList.getAllSorted()
            .filter { canViewIncident(currentUser, it) }
            .filter { !_onlyMine.value || isAuthor(currentUser, it) }
    }
}
