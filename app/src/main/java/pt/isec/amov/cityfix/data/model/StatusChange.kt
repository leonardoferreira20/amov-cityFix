package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusChange(
    val fromStatus: IncidentStatus,
    val toStatus: IncidentStatus,
    val timestamp: Long,
    val changedBy: String,
    val note: String? = null
)
