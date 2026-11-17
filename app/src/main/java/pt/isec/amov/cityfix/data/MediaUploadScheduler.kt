package pt.isec.amov.cityfix.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.UploadStatus
import pt.isec.amov.cityfix.workers.MediaUploadWorker
import java.io.File

class MediaUploadScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context)
    private val networkConstraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePendingUploads(incident: Incident) {
        incident.media
            .filter { it.uploadStatus == UploadStatus.PENDING }
            .forEach { media ->
                val localPath = media.localUri?.takeIf { File(it).exists() } ?: return@forEach
                val request = OneTimeWorkRequestBuilder<MediaUploadWorker>()
                    .setConstraints(networkConstraint)
                    .setInputData(
                        workDataOf(
                            MediaUploadWorker.KEY_INCIDENT_ID to incident.id,
                            MediaUploadWorker.KEY_MEDIA_ID to media.id,
                            MediaUploadWorker.KEY_LOCAL_PATH to localPath
                        )
                    )
                    .build()
                workManager.enqueueUniqueWork("upload-${media.id}", ExistingWorkPolicy.KEEP, request)
            }
    }
}
