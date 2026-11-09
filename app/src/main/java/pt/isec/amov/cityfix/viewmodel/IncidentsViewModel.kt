package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentsList

class IncidentsViewModel(private val repository: IncidentRepository) : ViewModel() {
    private val _incidentsList = MutableStateFlow<List<Incident>>(emptyList())
    val incidentsList: StateFlow<List<Incident>> = _incidentsList.asStateFlow()
    private var currentIncidentsList = IncidentsList()

    init {
        refreshIncidents()
    }

    fun refreshIncidents() {
        viewModelScope.launch {
            currentIncidentsList = repository.loadIncidents()
            _incidentsList.value = currentIncidentsList.getAllSorted()
        }
    }

    fun getIncidentById(id: String): Incident? = currentIncidentsList.findById(id)
}
