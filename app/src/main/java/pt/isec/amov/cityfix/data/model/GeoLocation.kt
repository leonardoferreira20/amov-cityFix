package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
