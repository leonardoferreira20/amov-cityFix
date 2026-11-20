package pt.isec.amov.cityfix.ui.components

import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.IncidentStatus
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun IncidentCard(
    incident: Incident,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(
                    if (isExpanded) R.string.incident_collapse else R.string.incident_expand
                ),
                role = Role.Button,
                onClick = onToggleExpanded
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IncidentPhoto(
                path = incident.firstThumbnailPath(),
                contentDescription = stringResource(R.string.incident_photo_description),
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = incident.categoryName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(incident.situation.labelRes()),
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(incident.status.labelRes()),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(text = incident.description, style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = onOpenDetails, modifier = Modifier.align(Alignment.End)) {
                            Text(stringResource(R.string.incident_open))
                        }
                    }
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
    }
}

private val previewIncident = Incident(
    id = "preview",
    categoryName = "Vias Públicas",
    situation = Situation.DAMAGE,
    description = "Buraco grande na faixa da direita, junto à passadeira.",
    status = IncidentStatus.OPEN
)

@Preview(showBackground = true)
@Composable
private fun IncidentCardPreview() {
    CityFixTheme {
        IncidentCard(incident = previewIncident, isExpanded = false, onToggleExpanded = {}, onOpenDetails = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentCardExpandedPreview() {
    CityFixTheme {
        IncidentCard(
            incident = previewIncident.copy(status = IncidentStatus.IN_PROGRESS),
            isExpanded = true,
            onToggleExpanded = {},
            onOpenDetails = {}
        )
    }
}
