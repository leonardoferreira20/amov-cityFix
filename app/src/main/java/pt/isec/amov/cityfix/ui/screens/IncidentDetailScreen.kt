package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import pt.isec.amov.cityfix.ui.components.firstPhotoPath
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.viewmodel.IncidentDetailUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentDetailScreen(
    state: IncidentDetailUiState,
    modifier: Modifier = Modifier,
    operatorActions: @Composable () -> Unit = {}
) {
    if (state.isLoading) return
    val incident = state.incident
    if (incident == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.incident_not_found))
        }
        return
    }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        incident.firstPhotoPath()?.let { path ->
            IncidentPhoto(
                path = path,
                contentDescription = stringResource(R.string.incident_photo_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
        Text(text = incident.categoryName, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = stringResource(incident.situation.labelRes()),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.incident_status, stringResource(incident.status.labelRes())),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.detail_created, dateFormat.format(Date(incident.createdAt))),
            style = MaterialTheme.typography.bodyMedium
        )

        DetailSection(title = stringResource(R.string.detail_description)) {
            Text(text = incident.description, style = MaterialTheme.typography.bodyLarge)
        }

        if (state.showLocation) {
            DetailSection(title = stringResource(R.string.detail_location_title)) {
                Text(
                    text = stringResource(
                        R.string.detail_location,
                        incident.location.latitude,
                        incident.location.longitude
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
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
                            dateFormat.format(Date(change.timestamp)),
                            stringResource(change.fromStatus.labelRes()),
                            stringResource(change.toStatus.labelRes())
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (state.showOperatorActions) {
            operatorActions()
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall)
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
