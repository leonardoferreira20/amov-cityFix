package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProximityAlert(
    val operatorId: String,
    val timestamp: Long
)
