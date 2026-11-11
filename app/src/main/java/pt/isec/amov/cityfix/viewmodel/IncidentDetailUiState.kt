package pt.isec.amov.cityfix.viewmodel

import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.User

data class IncidentDetailUiState(
    val isLoading: Boolean = true,
    val incident: Incident? = null,
    val author: User? = null,
    val showLocation: Boolean = false,
    val showHistory: Boolean = false,
    val showOperatorActions: Boolean = false
)
