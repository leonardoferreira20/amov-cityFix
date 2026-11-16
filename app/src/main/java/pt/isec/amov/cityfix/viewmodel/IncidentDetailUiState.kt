package pt.isec.amov.cityfix.viewmodel

import androidx.annotation.StringRes

import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.InfoRequest
import pt.isec.amov.cityfix.data.model.Message
import pt.isec.amov.cityfix.data.model.User

data class IncidentDetailUiState(
    val isLoading: Boolean = true,
    val incident: Incident? = null,
    val author: User? = null,
    val showLocation: Boolean = false,
    val showHistory: Boolean = false,
    val showOperatorActions: Boolean = false,
    val canStartWork: Boolean = false,
    val canResolve: Boolean = false,
    val canReopen: Boolean = false,
    val reopenDeadline: Long? = null,
    val showInformationRequests: Boolean = false,
    val canRequestInformation: Boolean = false,
    val informationRequests: List<InformationRequestItem> = emptyList(),
    val isSavingInformation: Boolean = false,
    val groupSize: Int = 1,
    val showDirections: Boolean = false,
    val showProximityAlerts: Boolean = false,
    @StringRes val actionError: Int? = null
)

data class InformationRequestItem(
    val request: InfoRequest,
    val reply: Message?,
    val canReply: Boolean
)
