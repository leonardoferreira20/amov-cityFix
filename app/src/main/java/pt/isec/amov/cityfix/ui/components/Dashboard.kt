package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun Dashboard(
    stats: DashboardStats,
    incidents: List<Incident>,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        DashboardStatsCard(
            stats = stats,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(modifier = Modifier.weight(1f)) {
            item {
                DashboardPage(
                    title = stringResource(R.string.recent_incidents_title),
                    modifier = Modifier.fillParentMaxSize()
                ) {
                    IncidentList(
                        incidents = incidents,
                        expandedIncidentIds = expandedIncidentIds,
                        onToggleIncident = onToggleIncident,
                        onOpenIncident = onOpenIncident,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            item {
                DashboardPage(
                    title = stringResource(R.string.incidents_by_category),
                    modifier = Modifier.fillParentMaxSize()
                ) {
                    IncidentChart(
                        counts = incidents.groupingBy { it.categoryName }.eachCount(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardPage(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}
