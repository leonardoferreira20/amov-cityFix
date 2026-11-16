package pt.isec.amov.cityfix.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.utils.FileUtils

@Composable
fun PhotoPicker(
    photoPath: String?,
    onPhotoChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    previewHeight: Dp = 200.dp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IncidentPhoto(
            path = photoPath,
            contentDescription = stringResource(R.string.incident_photo_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
        )
        PhotoSourceButtons(onPhotoChange = onPhotoChange)
    }
}

@Composable
fun PhotoSourceButtons(
    onPhotoChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    val context = LocalContext.current
    var cameraFilePath by rememberSaveable { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoChange(FileUtils.copyUriToInternalStorage(context, it)) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) onPhotoChange(cameraFilePath) else FileUtils.deleteFromPath(cameraFilePath)
    }

    val launchCamera = {
        val (file, uri) = FileUtils.createTempImageFile(context)
        cameraFilePath = file.absolutePath
        cameraLauncher.launch(uri)
    }
    var cameraDenied by rememberSaveable { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraDenied = !granted
        if (granted) launchCamera()
    }

    val buttons: @Composable () -> Unit = {
        Button(onClick = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(stringResource(R.string.photo_gallery))
        }
        OutlinedButton(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                launchCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(stringResource(R.string.photo_camera))
        }
    }

    if (cameraDenied) {
        Text(
            text = stringResource(R.string.camera_permission_denied),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (vertical) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) { buttons() }
    } else {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) { buttons() }
    }
}
