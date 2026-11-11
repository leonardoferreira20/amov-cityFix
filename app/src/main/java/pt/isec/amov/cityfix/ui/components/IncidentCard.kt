package pt.isec.amov.cityfix.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun IncidentCard(
    incident: Incident,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = stringResource(R.string.incident_open), onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IncidentPhoto(
                path = incident.firstPhotoPath(),
                contentDescription = stringResource(R.string.incident_photo_description),
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = incident.categoryName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(incident.situation.labelRes()),
                    style = MaterialTheme.typography.bodyMedium
                )
                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = stringResource(
                                R.string.incident_status,
                                stringResource(incident.status.labelRes())
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(text = incident.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
