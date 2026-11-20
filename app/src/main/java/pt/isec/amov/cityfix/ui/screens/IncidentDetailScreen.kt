package pt.isec.amov.cityfix.ui.screens

import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import pt.isec.amov.cityfix.ui.components.IncidentDirections
import pt.isec.amov.cityfix.ui.components.IncidentMap
import pt.isec.amov.cityfix.utils.toDisplayText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.StatusChange
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.components.IncidentPhoto
import pt.isec.amov.cityfix.ui.components.InformationRequests
import pt.isec.amov.cityfix.ui.components.PhotoPicker
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.ui.components.hasPendingUploads
import pt.isec.amov.cityfix.ui.components.IncidentVideo
import pt.isec.amov.cityfix.ui.components.photoPaths
import pt.isec.amov.cityfix.ui.components.videoPaths
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.utils.formatDateTime
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.viewmodel.IncidentDetailUiState

@Composable
fun IncidentDetailScreen(
    state: IncidentDetailUiState,
    modifier: Modifier = Modifier,
    onReopen: (String, String) -> Unit = { _, _ -> },
    onRequestInformation: (String, (Int?) -> Unit) -> Unit = { _, _ -> },
    onReplyToInformationRequest: (String, String, (Int?) -> Unit) -> Unit = { _, _, _ -> },
    operatorActions: @Composable () -> Unit = {},
    deviceLocation: GeoLocation? = null
) {
    if (state.isLoading) return
    val incident = state.incident
    if (incident == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.incident_not_found))
        }
        return
    }
    var showReopenDialog by rememberSaveable { mutableStateOf(false) }
    var showInformationDialog by rememberSaveable { mutableStateOf(false) }
    var replyRequestId by rememberSaveable { mutableStateOf<String?>(null) }

    if (showReopenDialog) {
        ReopenDialog(
            onConfirm = { justification, photoPath ->
                showReopenDialog = false
                onReopen(justification, photoPath)
            },
            onDismiss = { showReopenDialog = false }
        )
    }

    if (showInformationDialog) {
        InformationMessageDialog(
            isReply = replyRequestId != null,
            isSaving = state.isSavingInformation,
            onSend = { text, onResult ->
                val requestId = replyRequestId
                if (requestId == null) {
                    onRequestInformation(text, onResult)
                } else {
                    onReplyToInformationRequest(requestId, text, onResult)
                }
            },
            onDismiss = { showInformationDialog = false }
        )
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val photos = incident.photoPaths()
    val videos = incident.videoPaths()

    val photosContent: @Composable () -> Unit = {
        if (photos.isNotEmpty() || videos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(photos) { path ->
                        IncidentPhoto(
                            path = path,
                            contentDescription = stringResource(R.string.incident_photo_description),
                            modifier = Modifier
                                .fillParentMaxWidth(if (photos.size == 1) 1f else 0.85f)
                                .height(220.dp)
                        )
                    }
                }
                videos.forEach { path ->
                    IncidentVideo(
                        path = path,
                        contentDescription = stringResource(R.string.incident_video_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
                if (incident.hasPendingUploads()) {
                    Text(
                        text = stringResource(R.string.detail_photos_pending),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    val mapContent: @Composable (Modifier) -> Unit = { mapModifier ->
        IncidentMap(
            location = incident.location,
            exact = state.showLocation,
            contentDescription = stringResource(
                if (state.showLocation) R.string.map_incident_description else R.string.map_area_description
            ),
            modifier = mapModifier.clip(RoundedCornerShape(12.dp))
        )
    }

    val detailsContent: @Composable (Modifier, Boolean) -> Unit = { detailsModifier, showMapInline ->
        Column(modifier = detailsModifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = incident.categoryName, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
            Text(
                text = stringResource(incident.situation.labelRes()),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.incident_status, stringResource(incident.status.labelRes())),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.detail_created, formatDateTime(incident.createdAt)),
                style = MaterialTheme.typography.bodyMedium
            )
            if (state.groupSize > 1) {
                Text(
                    text = stringResource(R.string.detail_group, state.groupSize),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val reopenDeadline = state.reopenDeadline
            if (state.canReopen && reopenDeadline != null) {
                Button(onClick = { showReopenDialog = true }) {
                    Text(stringResource(R.string.reopen_button))
                }
                Text(
                    text = stringResource(R.string.reopen_deadline, formatDateTime(reopenDeadline)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            DetailSection(title = stringResource(R.string.detail_description)) {
                Text(text = incident.description, style = MaterialTheme.typography.bodyLarge)
            }

            if (state.showLocation || state.showDirections || showMapInline) {
                DetailSection(title = stringResource(R.string.detail_location_title)) {
                    if (state.showLocation) {
                        Text(text = incident.location.toDisplayText(), style = MaterialTheme.typography.bodyLarge)
                    }
                    if (state.showDirections) {
                        IncidentDirections(location = incident.location, deviceLocation = deviceLocation)
                    }
                    if (showMapInline) {
                        mapContent(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            if (state.showProximityAlerts) {
                DetailSection(title = stringResource(R.string.proximity_title)) {
                    incident.proximityAlerts.asReversed().forEach { alert ->
                        Text(
                            text = stringResource(R.string.proximity_entry, formatDateTime(alert.timestamp)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            state.author?.let { author ->
                DetailSection(title = stringResource(R.string.detail_author)) {
                    Text(text = author.name, style = MaterialTheme.typography.bodyLarge)
                    Text(text = author.email, style = MaterialTheme.typography.bodyMedium)
                    Text(text = author.phone, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (state.showHistory) {
                DetailSection(title = stringResource(R.string.detail_history)) {
                    if (incident.history.isEmpty()) {
                        Text(
                            text = stringResource(R.string.detail_history_empty),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    incident.history.forEach { change ->
                        Text(
                            text = stringResource(
                                R.string.detail_history_entry,
                                formatDateTime(change.timestamp),
                                stringResource(change.fromStatus.labelRes()),
                                stringResource(change.toStatus.labelRes())
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        change.note?.let { note ->
                            Text(
                                text = stringResource(R.string.detail_history_note, note),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            if (state.showOperatorActions) {
                DetailSection(title = stringResource(R.string.lifecycle_title)) {
                    operatorActions()
                }
            }

            if (state.showInformationRequests) {
                DetailSection(title = stringResource(R.string.information_requests_title)) {
                    InformationRequests(
                        requests = state.informationRequests,
                        canRequest = state.canRequestInformation,
                        onRequest = {
                            replyRequestId = null
                            showInformationDialog = true
                        },
                        onReply = { requestId ->
                            replyRequestId = requestId
                            showInformationDialog = true
                        }
                    )
                }
            }
        }
    }

    if (isLandscape) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            detailsContent(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                false
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                photosContent()
                mapContent(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            photosContent()
            detailsContent(Modifier.fillMaxWidth(), true)
        }
    }
}

@Composable
private fun InformationMessageDialog(
    isReply: Boolean,
    isSaving: Boolean,
    onSend: (String, (Int?) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<Int?>(null) }
    val title = if (isReply) R.string.information_reply_action else R.string.information_request_action

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(stringResource(title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.information_message_label)) },
                enabled = !isSaving,
                isError = error != null,
                supportingText = { error?.let { Text(stringResource(it)) } },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                enabled = text.isNotBlank() && !isSaving,
                onClick = {
                    onSend(text) { result ->
                        error = result
                        if (result == null) onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.information_send_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ReopenDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var justification by rememberSaveable { mutableStateOf("") }
    var photoPath by rememberSaveable { mutableStateOf<String?>(null) }
    val cancel = {
        FileUtils.deleteFromPath(photoPath)
        onDismiss()
    }
    AlertDialog(
        onDismissRequest = cancel,
        title = { Text(stringResource(R.string.reopen_dialog_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.reopen_dialog_text))
                PhotoPicker(
                    photoPath = photoPath,
                    onPhotoChange = { newPath ->
                        if (photoPath != null && photoPath != newPath) FileUtils.deleteFromPath(photoPath)
                        photoPath = newPath
                    },
                    previewHeight = 140.dp
                )
                OutlinedTextField(
                    value = justification,
                    onValueChange = { justification = it },
                    label = { Text(stringResource(R.string.reopen_justification)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { photoPath?.let { onConfirm(justification, it) } },
                enabled = justification.isNotBlank() && photoPath != null
            ) {
                Text(stringResource(R.string.reopen_button))
            }
        },
        dismissButton = {
            TextButton(onClick = cancel) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.semantics { heading() })
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentDetailScreenOperatorPreview() {
    CityFixTheme {
        IncidentDetailScreen(
            state = IncidentDetailUiState(
                isLoading = false,
                incident = Incident(
                    authorId = "citizen-1",
                    categoryId = "cat-roads",
                    categoryName = "Vias Públicas",
                    situation = Situation.DAMAGE,
                    description = "Buraco grande na faixa da direita.",
                    visibility = Visibility.PUBLIC,
                    location = GeoLocation(40.2089, -8.4194),
                    status = IncidentStatus.IN_PROGRESS,
                    history = listOf(
                        StatusChange(
                            fromStatus = IncidentStatus.OPEN,
                            toStatus = IncidentStatus.IN_PROGRESS,
                            timestamp = 0L,
                            changedBy = "operator-1"
                        )
                    ),
                    createdAt = 0L
                ),
                author = User(
                    name = "Bruno Costa",
                    email = "bruno.costa@example.com",
                    phone = "912000001",
                    nif = "234567890",
                    role = UserRole.CITIZEN,
                    status = AccountStatus.ACTIVE,
                    createdAt = 0L
                ),
                showLocation = true,
                showHistory = true
            )
        )
    }
}
