package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val phone: String,
    val nif: String,
    val role: UserRole,
    val status: AccountStatus,
    val createdAt: Long,
    val picturePath: String? = null,
    val location: GeoLocation? = null,
    val impactDetectionEnabled: Boolean = false,
    val address: String? = null
)
