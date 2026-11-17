package pt.isec.amov.cityfix.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {
    fun compress(source: File, destination: File, maxSize: Int, quality: Int) {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.path, bounds)

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, maxSize)
        }
        val decoded = checkNotNull(BitmapFactory.decodeFile(source.path, options))

        val scale = minOf(1f, maxSize.toFloat() / maxOf(decoded.width, decoded.height))
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postRotate(rotationDegrees(source).toFloat())
        }
        val result = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)

        FileOutputStream(destination).use { output ->
            result.compress(Bitmap.CompressFormat.JPEG, quality, output)
        }
    }

    private fun sampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sampleSize = 1
        while (maxOf(width, height) / (sampleSize * 2) >= maxSize) sampleSize *= 2
        return sampleSize
    }

    private fun rotationDegrees(source: File): Int =
        when (ExifInterface(source.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
}
