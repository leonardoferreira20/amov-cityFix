package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class InfoRequest(
    val id: String = UUID.randomUUID().toString(),
    val incidentId: String,
    val requestedBy: String,
    val requesterRole: UserRole,
    val text: String,
    val status: InfoRequestStatus = InfoRequestStatus.OPEN,
    val createdAt: Long,
    val answeredAt: Long? = null
)
