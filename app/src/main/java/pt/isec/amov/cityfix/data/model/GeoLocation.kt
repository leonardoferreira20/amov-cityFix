package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)
