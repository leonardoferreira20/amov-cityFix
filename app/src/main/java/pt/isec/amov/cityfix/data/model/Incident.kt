package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Incident(
    val id: String = UUID.randomUUID().toString(),
    val authorId: String,
    val categoryId: String,
    val categoryName: String,
    val situation: Situation,
    val description: String,
    val visibility: Visibility,
    val location: GeoLocation,
    val status: IncidentStatus = IncidentStatus.OPEN,
    val previousStatus: IncidentStatus? = null,
    val media: List<MediaAttachment> = emptyList(),
    val history: List<StatusChange> = emptyList(),
    val primaryIncidentId: String? = null,
    val createdAt: Long,
    val workStartedAt: Long? = null,
    val resolvedAt: Long? = null,
    val closedAt: Long? = null,
    val reopenCount: Int = 0
)
