package pt.isec.amov.cityfix.workers

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import pt.isec.amov.cityfix.CityFixApp
import pt.isec.amov.cityfix.data.model.MediaType
import pt.isec.amov.cityfix.utils.ImageCompressor
import java.io.File

class MediaUploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val incidentId = inputData.getString(KEY_INCIDENT_ID) ?: return Result.failure()
        val mediaId = inputData.getString(KEY_MEDIA_ID) ?: return Result.failure()
        val source = inputData.getString(KEY_LOCAL_PATH)?.let(::File)?.takeIf { it.exists() }
            ?: return Result.failure()
        val folder = Firebase.storage.reference.child("incidents/$incidentId")

        return try {
            val (remoteUrl, thumbnailUrl) = if (inputData.getString(KEY_MEDIA_TYPE) == MediaType.VIDEO.name) {
                uploadVideo(folder, mediaId, source)
            } else {
                uploadImage(folder, mediaId, source)
            }
            (applicationContext as CityFixApp).incidentRepository
                .markMediaUploaded(incidentId, mediaId, remoteUrl, thumbnailUrl)
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    private suspend fun uploadImage(folder: StorageReference, mediaId: String, source: File): Pair<String, String?> {
        val image = File(applicationContext.cacheDir, "$mediaId.jpg")
        val thumbnail = File(applicationContext.cacheDir, "${mediaId}_thumb.jpg")
        try {
            ImageCompressor.compress(source, image, IMAGE_MAX_SIZE, IMAGE_QUALITY)
            ImageCompressor.compress(source, thumbnail, THUMBNAIL_MAX_SIZE, THUMBNAIL_QUALITY)
            val imageUrl = upload(folder.child(image.name), image, IMAGE_MIME_TYPE)
            val thumbnailUrl = upload(folder.child(thumbnail.name), thumbnail, IMAGE_MIME_TYPE)
            return imageUrl to thumbnailUrl
        } finally {
            image.delete()
            thumbnail.delete()
        }
    }

    private suspend fun uploadVideo(folder: StorageReference, mediaId: String, source: File): Pair<String, String?> {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(source.extension) ?: VIDEO_MIME_TYPE
        return upload(folder.child("$mediaId.${source.extension}"), source, mimeType) to null
    }

    private suspend fun upload(reference: StorageReference, file: File, mimeType: String): String {
        reference.putFile(Uri.fromFile(file), storageMetadata { contentType = mimeType }).await()
        return reference.downloadUrl.await().toString()
    }

    companion object {
        const val KEY_INCIDENT_ID = "incidentId"
        const val KEY_MEDIA_ID = "mediaId"
        const val KEY_LOCAL_PATH = "localPath"
        const val KEY_MEDIA_TYPE = "mediaType"
        private const val IMAGE_MAX_SIZE = 1280
        private const val IMAGE_QUALITY = 80
        private const val THUMBNAIL_MAX_SIZE = 320
        private const val THUMBNAIL_QUALITY = 70
        private const val IMAGE_MIME_TYPE = "image/jpeg"
        private const val VIDEO_MIME_TYPE = "video/mp4"
        private const val MAX_RETRIES = 5
    }
}
