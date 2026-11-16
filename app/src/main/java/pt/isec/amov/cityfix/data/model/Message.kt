package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val infoRequestId: String = "",
    val senderId: String = "",
    val senderRole: UserRole = UserRole.CITIZEN,
    val text: String = "",
    val timestamp: Long = 0L
)
