package pt.isec.amov.cityfix.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtils {
    private fun imagesDir(context: Context): File =
        File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

    private fun videosDir(context: Context): File =
        File(context.filesDir, "videos").apply { if (!exists()) mkdirs() }

    fun copyUriToInternalStorage(context: Context, sourceUri: Uri): String? =
        copy(context, sourceUri, File(imagesDir(context), "IMG_${UUID.randomUUID()}.jpg"))

    fun copyVideoToInternalStorage(context: Context, sourceUri: Uri): String? {
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver.getType(sourceUri)) ?: "mp4"
        return copy(context, sourceUri, File(videosDir(context), "VID_${UUID.randomUUID()}.$extension"))
    }

    private fun copy(context: Context, sourceUri: Uri, destinationFile: File): String? = try {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destinationFile).use { output -> input.copyTo(output) }
        }
        destinationFile.absolutePath
    } catch (_: Exception) {
        null
    }

    fun sizeOf(context: Context, uri: Uri): Long? = try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else null
        }
    } catch (_: Exception) {
        null
    }

    fun createTempImageFile(context: Context): Pair<File, Uri> =
        tempFile(context, File(imagesDir(context), "CAPTURE_${System.currentTimeMillis()}.jpg"))

    fun createTempVideoFile(context: Context): Pair<File, Uri> =
        tempFile(context, File(videosDir(context), "CAPTURE_${System.currentTimeMillis()}.mp4"))

    private fun tempFile(context: Context, file: File): Pair<File, Uri> {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Pair(file, uri)
    }

    fun deleteFromPath(path: String?): Boolean {
        if (path.isNullOrBlank()) return false
        return try {
            File(path).delete()
        } catch (_: Exception) {
            false
        }
    }
}
