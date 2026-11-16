package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusChange(
    val fromStatus: IncidentStatus = IncidentStatus.OPEN,
    val toStatus: IncidentStatus = IncidentStatus.OPEN,
    val timestamp: Long = 0L,
    val changedBy: String = "",
    val note: String? = null
)
