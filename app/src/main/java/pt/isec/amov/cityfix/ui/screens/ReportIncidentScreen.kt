package pt.isec.amov.cityfix.ui.screens

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Category
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.components.PhotoPicker
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
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
            PhotoPicker(photoPath = state.photoPath, onPhotoChange = onPhotoChange)
            ErrorText(state.photoError)

            DropdownField(
                label = R.string.report_category,
                options = state.categories,
                selected = state.categories.find { it.id == state.categoryId },
                optionLabel = { it.name },
                onSelect = { onCategorySelected(it.id) },
                error = state.categoryError
            )
            if (state.categories.isEmpty()) {
                Text(stringResource(R.string.report_no_categories), style = MaterialTheme.typography.bodyMedium)
            }

            DropdownField(
                label = R.string.report_situation,
                options = Situation.entries,
                selected = state.situation,
                optionLabel = { stringResource(it.labelRes()) },
                onSelect = onSituationSelected,
                error = state.situationError
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownField(
    @StringRes label: Int,
    options: List<T>,
    selected: T?,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    @StringRes error: Int?
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.let { optionLabel(it) }.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = error != null,
            supportingText = { error?.let { Text(stringResource(it)) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
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
