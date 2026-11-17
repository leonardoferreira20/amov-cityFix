package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.MediaAttachment
import pt.isec.amov.cityfix.data.model.MediaType
import pt.isec.amov.cityfix.data.model.UploadStatus

private fun Incident.images(): List<MediaAttachment> = media.filter { it.type == MediaType.IMAGE }

fun Incident.photoPaths(): List<String> = images().mapNotNull { it.remoteUrl ?: it.localUri }

fun Incident.firstThumbnailPath(): String? = images().firstOrNull()?.let { it.thumbnailUrl ?: it.localUri }

fun Incident.hasPendingUploads(): Boolean = media.any { it.uploadStatus == UploadStatus.PENDING }

@Composable
fun IncidentPhoto(
    path: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    var cannotLoad by remember(path) { mutableStateOf(false) }
    if (path.isNullOrBlank() || cannotLoad) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        AsyncImage(
            model = path,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            onError = { cannotLoad = true },
            modifier = modifier.clip(shape)
        )
    }
}
