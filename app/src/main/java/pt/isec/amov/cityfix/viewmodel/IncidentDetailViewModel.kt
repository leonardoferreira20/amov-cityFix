package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.IncidentRepository
import pt.isec.amov.cityfix.data.UserRepository
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentsList
import pt.isec.amov.cityfix.data.model.InfoRequest
import pt.isec.amov.cityfix.data.model.Message
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.reopened
import pt.isec.amov.cityfix.data.model.resolved
import pt.isec.amov.cityfix.data.model.withInformationReply
import pt.isec.amov.cityfix.data.model.withInformationRequest
import pt.isec.amov.cityfix.data.model.workStarted
import pt.isec.amov.cityfix.utils.canChangeStatus
import pt.isec.amov.cityfix.utils.canReopen
import pt.isec.amov.cityfix.utils.canResolve
import pt.isec.amov.cityfix.utils.canStartWork
import pt.isec.amov.cityfix.utils.canReplyToInformationRequest
import pt.isec.amov.cityfix.utils.canRequestInformation
import pt.isec.amov.cityfix.utils.canSeeAuthorData
import pt.isec.amov.cityfix.utils.canSeeExactLocation
import pt.isec.amov.cityfix.utils.canSeeHistory
import pt.isec.amov.cityfix.utils.canSeeInformationRequests
import pt.isec.amov.cityfix.utils.canViewIncident
import pt.isec.amov.cityfix.utils.reopenDeadline
import java.io.IOException

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
            showIncident(incident)
        }
    }

    private suspend fun showIncident(incident: Incident?) {
        val author = if (incident != null && canSeeAuthorData(currentUser)) {
            userRepository.loadUsers().findById(incident.authorId)
        } else {
            null
        }
        val showRequests = incident != null && canSeeInformationRequests(currentUser, incident)
        val requests = if (incident != null && showRequests) {
            val replies = incident.messages.associateBy { it.infoRequestId }
            incident.infoRequests.asReversed().map { request ->
                InformationRequestItem(
                    request = request,
                    reply = replies[request.id],
                    canReply = canReplyToInformationRequest(currentUser, incident, request)
                )
            }
        } else {
            emptyList()
        }

        _detailState.update { state ->
            state.copy(
                isLoading = false,
                incident = incident,
                author = author,
                showLocation = incident != null && canSeeExactLocation(currentUser, incident),
                showHistory = incident != null && canSeeHistory(currentUser, incident),
                showOperatorActions = canChangeStatus(currentUser),
                canStartWork = incident != null && canStartWork(currentUser, incident),
                canResolve = incident != null && canResolve(currentUser, incident),
                canReopen = incident != null && canReopen(currentUser, incident),
                reopenDeadline = incident?.let { reopenDeadline(it) },
                showInformationRequests = showRequests,
                canRequestInformation = incident != null && canRequestInformation(currentUser, incident),
                informationRequests = requests
            )
        }
    }

    fun requestInformation(text: String, onResult: (Int?) -> Unit) {
        saveInformation(text, onResult) { incident, user, messageText ->
            if (!canRequestInformation(user, incident)) return@saveInformation null

            incident.withInformationRequest(
                InfoRequest(
                    incidentId = incident.id,
                    requestedBy = user.id,
                    requesterRole = user.role,
                    text = messageText,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun replyToInformationRequest(requestId: String, text: String, onResult: (Int?) -> Unit) {
        saveInformation(text, onResult) { incident, user, messageText ->
            val request = incident.infoRequests.find { it.id == requestId } ?: return@saveInformation null
            if (!canReplyToInformationRequest(user, incident, request)) return@saveInformation null

            incident.withInformationReply(
                request,
                Message(
                    infoRequestId = request.id,
                    senderId = user.id,
                    senderRole = user.role,
                    text = messageText,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun saveInformation(
        text: String,
        onResult: (Int?) -> Unit,
        change: (Incident, User, String) -> Incident?
    ) {
        if (_detailState.value.isSavingInformation) return
        val user = currentUser
        if (user == null || text.isBlank()) {
            onResult(R.string.information_action_unavailable)
            return
        }

        _detailState.update { it.copy(isSavingInformation = true) }
        viewModelScope.launch {
            val error = try {
                val incidents = incidentRepository.loadIncidents()
                val updated = incidents.findById(incidentId)?.let { change(it, user, text.trim()) }
                if (updated == null) {
                    R.string.information_action_unavailable
                } else {
                    val updatedIncidents = IncidentsList(incidents.rawList()).apply { upsert(updated) }
                    incidentRepository.saveIncidents(updatedIncidents)
                    showIncident(updated)
                    null
                }
            } catch (_: IOException) {
                R.string.information_save_error
            } finally {
                _detailState.update { it.copy(isSavingInformation = false) }
            }
            onResult(error)
        }
    }

    fun startWork(onSuccess: () -> Unit) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val incidents = incidentRepository.loadIncidents()
            val incident = incidents.findById(incidentId) ?: return@launch
            if (!canStartWork(user, incident)) return@launch
            incidents.upsert(incident.workStarted(user.id, System.currentTimeMillis()))
            incidentRepository.saveIncidents(incidents)
            refresh()
            onSuccess()
        }
    }

    fun resolve(photoPath: String?, note: String, onSuccess: () -> Unit) {
        val user = currentUser ?: return
        if (photoPath == null) return
        viewModelScope.launch {
            val incidents = incidentRepository.loadIncidents()
            val incident = incidents.findById(incidentId) ?: return@launch
            if (!canResolve(user, incident)) return@launch
            incidents.upsert(
                incident.resolved(user.id, photoPath, note.trim().ifBlank { null }, System.currentTimeMillis())
            )
            incidentRepository.saveIncidents(incidents)
            refresh()
            onSuccess()
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
