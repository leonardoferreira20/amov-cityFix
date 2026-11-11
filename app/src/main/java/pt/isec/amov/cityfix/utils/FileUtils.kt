package pt.isec.amov.cityfix.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtils {
    private fun imagesDir(context: Context): File =
        File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

    fun copyUriToInternalStorage(context: Context, sourceUri: Uri): String? = try {
        val destinationFile = File(imagesDir(context), "IMG_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destinationFile).use { output -> input.copyTo(output) }
        }
        destinationFile.absolutePath
    } catch (_: Exception) {
        null
    }

    fun createTempImageFile(context: Context): Pair<File, Uri> {
        val tempFile = File(imagesDir(context), "CAPTURE_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
        return Pair(tempFile, uri)
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
