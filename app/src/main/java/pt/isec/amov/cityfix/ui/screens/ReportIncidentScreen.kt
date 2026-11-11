package pt.isec.amov.cityfix.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Category
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.components.IncidentPhoto
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.utils.FileUtils
import pt.isec.amov.cityfix.viewmodel.ReportIncidentUiState

@Composable
fun ReportIncidentScreen(
    state: ReportIncidentUiState,
    onCategorySelected: (String) -> Unit,
    onSituationSelected: (Situation) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onVisibilityChange: (Visibility) -> Unit,
    onPhotoChange: (String?) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
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

    Scaffold(
        modifier = modifier.imePadding(),
        floatingActionButton = {
            FloatingActionButton(onClick = onSaveClick) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.report_submit))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle(R.string.report_photo)
            IncidentPhoto(
                path = state.photoPath,
                contentDescription = stringResource(R.string.incident_photo_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text(stringResource(R.string.photo_gallery))
                }
                OutlinedButton(onClick = {
                    val (file, uri) = FileUtils.createTempImageFile(context)
                    cameraFilePath = file.absolutePath
                    cameraLauncher.launch(uri)
                }) {
                    Text(stringResource(R.string.photo_camera))
                }
            }
            ErrorText(state.photoError)

            SectionTitle(R.string.report_category)
            if (state.categories.isEmpty()) {
                Text(stringResource(R.string.report_no_categories), style = MaterialTheme.typography.bodyMedium)
            }
            RadioOptions(
                options = state.categories,
                selected = state.categories.find { it.id == state.categoryId },
                label = { it.name },
                onSelect = { onCategorySelected(it.id) }
            )
            ErrorText(state.categoryError)

            SectionTitle(R.string.report_situation)
            RadioOptions(
                options = Situation.entries,
                selected = state.situation,
                label = { stringResource(it.labelRes()) },
                onSelect = onSituationSelected
            )
            ErrorText(state.situationError)

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.detail_description)) },
                isError = state.descriptionError != null,
                supportingText = { state.descriptionError?.let { Text(stringResource(it)) } },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            SectionTitle(R.string.report_visibility)
            RadioOptions(
                options = Visibility.entries,
                selected = state.visibility,
                label = { stringResource(it.labelRes()) },
                onSelect = onVisibilityChange
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun SectionTitle(@StringRes title: Int) {
    Text(stringResource(title), style = MaterialTheme.typography.titleSmall)
}

@Composable
private fun ErrorText(@StringRes error: Int?) {
    if (error != null) {
        Text(
            text = stringResource(error),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun <T> RadioOptions(
    options: List<T>,
    selected: T?,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option == selected,
                        onClick = { onSelect(option) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = option == selected, onClick = null)
                Text(text = label(option), modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportIncidentScreenPreview() {
    CityFixTheme {
        ReportIncidentScreen(
            state = ReportIncidentUiState(
                categories = listOf(
                    Category(id = "cat-roads", name = "Vias Públicas", active = true, createdBy = "operator-1", createdAt = 0L),
                    Category(id = "cat-water", name = "Água", active = true, createdBy = "operator-1", createdAt = 0L)
                ),
                categoryId = "cat-roads",
                situation = Situation.DAMAGE,
                photoError = R.string.error_photo_required
            ),
            onCategorySelected = {},
            onSituationSelected = {},
            onDescriptionChange = {},
            onVisibilityChange = {},
            onPhotoChange = {},
            onSaveClick = {}
        )
    }
}
