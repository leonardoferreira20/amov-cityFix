package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun IncidentList(
    incidents: List<Incident>,
    isExpanded: Boolean,
    onIncidentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (incidents.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_incidents))
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = incidents, key = { it.id }) { incident ->
                IncidentCard(
                    incident = incident,
                    isExpanded = isExpanded,
                    onClick = { onIncidentClick(incident.id) }
                )
            }
        }
    }
}
