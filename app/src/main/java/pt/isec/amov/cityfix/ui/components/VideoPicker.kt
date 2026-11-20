package pt.isec.amov.cityfix.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.MAX_VIDEO_BYTES
import pt.isec.amov.cityfix.utils.MAX_VIDEO_SECONDS

private const val BYTES_PER_MB = 1024 * 1024

@Composable
fun VideoPicker(
    videoPath: String?,
    onVideoChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    previewHeight: Dp = 200.dp
) {
    val context = LocalContext.current
    val maxMegabytes = (MAX_VIDEO_BYTES / BYTES_PER_MB).toInt()
    var cameraFilePath by rememberSaveable { mutableStateOf<String?>(null) }
    var cameraDenied by rememberSaveable { mutableStateOf(false) }
    var videoTooLarge by rememberSaveable { mutableStateOf(false) }
    var copyFailed by rememberSaveable { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        videoTooLarge = (FileUtils.sizeOf(context, uri) ?: 0L) > MAX_VIDEO_BYTES
        if (videoTooLarge) return@rememberLauncherForActivityResult
        val path = FileUtils.copyVideoToInternalStorage(context, uri)
        copyFailed = path == null
        if (path != null) onVideoChange(path)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = CaptureShortVideo()
    ) { success ->
        val path = cameraFilePath
        if (success && path != null) {
            videoTooLarge = false
            copyFailed = false
            onVideoChange(path)
        } else {
            FileUtils.deleteFromPath(path)
        }
    }

    val launchCamera = {
        val (file, uri) = FileUtils.createTempVideoFile(context)
        cameraFilePath = file.absolutePath
        cameraLauncher.launch(uri)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraDenied = !granted
        if (granted) launchCamera()
    }

    val galleryDescription = stringResource(R.string.video_gallery_description)
    val recordDescription = stringResource(R.string.video_record_description)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        videoPath?.let { path ->
            IncidentVideo(
                path = path,
                contentDescription = stringResource(R.string.incident_video_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(previewHeight)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                },
                modifier = Modifier.semantics { contentDescription = galleryDescription }
            ) {
                Text(stringResource(R.string.video_gallery))
            }
            OutlinedButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        launchCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.semantics { contentDescription = recordDescription }
            ) {
                Text(stringResource(R.string.video_record))
            }
        }
        if (videoPath != null) {
            TextButton(onClick = { onVideoChange(null) }) {
                Text(stringResource(R.string.video_remove))
            }
        }
        Text(
            text = stringResource(R.string.video_limits, MAX_VIDEO_SECONDS, maxMegabytes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val error = when {
            videoTooLarge -> stringResource(R.string.video_too_large, maxMegabytes)
            copyFailed -> stringResource(R.string.video_copy_failed)
            cameraDenied -> stringResource(R.string.camera_permission_denied)
            else -> null
        }
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private class CaptureShortVideo : ActivityResultContracts.CaptureVideo() {
    override fun createIntent(context: Context, input: Uri): Intent =
        super.createIntent(context, input)
            .putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_SECONDS)
            .putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_BYTES)
}
