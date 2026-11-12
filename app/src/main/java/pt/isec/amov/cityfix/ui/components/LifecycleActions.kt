package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LifecycleActions(
    status: IncidentStatus,
    closesAt: Long?,
    canStartWork: Boolean,
    canResolve: Boolean,
    onStartWork: () -> Unit,
    onResolve: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showResolveDialog by rememberSaveable { mutableStateOf(false) }

    if (showResolveDialog) {
        ResolveDialog(
            onConfirm = { photoPath, note ->
                showResolveDialog = false
                onResolve(photoPath, note)
            },
            onDismiss = { showResolveDialog = false }
        )
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (canStartWork) {
            Button(onClick = onStartWork) {
                Text(stringResource(R.string.lifecycle_start_work))
            }
        }
        if (canResolve) {
            Button(onClick = { showResolveDialog = true }) {
                Text(stringResource(R.string.lifecycle_resolve))
            }
        }
        lifecycleHint(status, closesAt)?.let { hint ->
            Text(text = hint, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun lifecycleHint(status: IncidentStatus, closesAt: Long?): String? = when {
    status == IncidentStatus.INFO_REQUESTED -> stringResource(R.string.lifecycle_waiting_reply)
    status == IncidentStatus.RESOLVED && closesAt != null -> stringResource(
        R.string.lifecycle_closes_on,
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(closesAt))
    )
    status == IncidentStatus.CLOSED -> stringResource(R.string.lifecycle_closed)
    else -> null
}

@Composable
private fun ResolveDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var note by rememberSaveable { mutableStateOf("") }
    var photoPath by rememberSaveable { mutableStateOf<String?>(null) }
    val cancel = {
        FileUtils.deleteFromPath(photoPath)
        onDismiss()
    }
    AlertDialog(
        onDismissRequest = cancel,
        title = { Text(stringResource(R.string.lifecycle_resolve_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.lifecycle_resolve_text))
                PhotoPicker(
                    photoPath = photoPath,
                    onPhotoChange = { newPath ->
                        if (photoPath != null && photoPath != newPath) FileUtils.deleteFromPath(photoPath)
                        photoPath = newPath
                    },
                    previewHeight = 140.dp
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.lifecycle_resolve_note)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { photoPath?.let { onConfirm(it, note) } },
                enabled = photoPath != null
            ) {
                Text(stringResource(R.string.lifecycle_resolve))
            }
        },
        dismissButton = {
            TextButton(onClick = cancel) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LifecycleActionsPreview() {
    CityFixTheme {
        LifecycleActions(
            status = IncidentStatus.IN_PROGRESS,
            closesAt = null,
            canStartWork = false,
            canResolve = true,
            onStartWork = {},
            onResolve = { _, _ -> }
        )
    }
}
