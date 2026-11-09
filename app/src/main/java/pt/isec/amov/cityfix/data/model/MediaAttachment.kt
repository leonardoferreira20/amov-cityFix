package pt.isec.amov.cityfix.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MediaAttachment(
    val id: String = UUID.randomUUID().toString(),
    val type: MediaType,
    val purpose: MediaPurpose,
    val localUri: String? = null,
    val remoteUrl: String? = null,
    val thumbnailUrl: String? = null,
    val uploadStatus: UploadStatus = UploadStatus.PENDING,
    val uploadedBy: String,
    val createdAt: Long
)
