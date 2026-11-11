package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.reopened
import pt.isec.amov.cityfix.utils.canChangeStatus
import pt.isec.amov.cityfix.utils.canReopen
import pt.isec.amov.cityfix.utils.reopenDeadline
import pt.isec.amov.cityfix.utils.canSeeAuthorData
import pt.isec.amov.cityfix.utils.canSeeExactLocation
import pt.isec.amov.cityfix.utils.canSeeHistory
import pt.isec.amov.cityfix.utils.canViewIncident

class IncidentDetailViewModel(
    private val incidentRepository: IncidentRepository,
    private val userRepository: UserRepository,
    private val incidentId: String,
    private val currentUser: User?
) : ViewModel() {
    private val _detailState = MutableStateFlow(IncidentDetailUiState())
    val detailState: StateFlow<IncidentDetailUiState> = _detailState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val incident = incidentRepository.loadIncidents().findById(incidentId)
                ?.takeIf { canViewIncident(currentUser, it) }
            val author = if (incident != null && canSeeAuthorData(currentUser)) {
                userRepository.loadUsers().findById(incident.authorId)
            } else {
                null
            }
            _detailState.value = IncidentDetailUiState(
                isLoading = false,
                incident = incident,
                author = author,
                showLocation = incident != null && canSeeExactLocation(currentUser, incident),
                showHistory = incident != null && canSeeHistory(currentUser, incident),
                showOperatorActions = canChangeStatus(currentUser),
                canReopen = incident != null && canReopen(currentUser, incident),
                reopenDeadline = incident?.let { reopenDeadline(it) }
            )
        }
    }

    fun reopen(justification: String, photoPath: String?, onSuccess: () -> Unit) {
        val user = currentUser ?: return
        if (justification.isBlank() || photoPath == null) return
        viewModelScope.launch {
            val incidents = incidentRepository.loadIncidents()
            val incident = incidents.findById(incidentId) ?: return@launch
            if (!canReopen(user, incident)) return@launch
            incidents.upsert(
                incident.reopened(user.id, justification.trim(), photoPath, System.currentTimeMillis())
            )
            incidentRepository.saveIncidents(incidents)
            refresh()
            onSuccess()
        }
    }
}
