package pt.isec.amov.cityfix.ui.components

import androidx.compose.ui.semantics.heading
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.ui.theme.CityFixTheme

@Composable
fun IncidentCharts(
    incidents: List<Incident>,
    modifier: Modifier = Modifier
) {
    val byCategory = remember(incidents) {
        incidents.groupingBy { it.categoryName }.eachCount()
            .toList().sortedByDescending { it.second }
    }
    val statusCounts = remember(incidents) { incidents.groupingBy { it.status }.eachCount() }
    val byStatus = IncidentStatus.entries.filter { it in statusCounts }.map {
        stringResource(it.labelRes()) to statusCounts.getValue(it)
    }

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(stringResource(R.string.dashboard_statistics), style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
            Text(stringResource(R.string.dashboard_visible_stats), style = MaterialTheme.typography.bodyMedium)
            if (incidents.isEmpty()) {
                Text(stringResource(R.string.chart_no_data), style = MaterialTheme.typography.bodyMedium)
            } else {
                IncidentDistribution(stringResource(R.string.chart_by_status), byStatus, incidents.size)
                HorizontalDivider()
                IncidentDistribution(stringResource(R.string.chart_by_category), byCategory, incidents.size)
            }
        }
    }
}

@Composable
private fun IncidentDistribution(title: String, counts: List<Pair<String, Int>>, total: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
        counts.forEach { (label, count) ->
            ChartBar(label = label, count = count, fraction = count.toFloat() / total)
        }
    }
}

@Composable
private fun ChartBar(
    label: String,
    count: Int,
    fraction: Float
) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(fraction).height(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentChartsPreview() {
    CityFixTheme {
        IncidentCharts(
            incidents = listOf(
                Incident(categoryName = "Vias Públicas"),
                Incident(categoryName = "Vias Públicas", status = IncidentStatus.IN_PROGRESS),
                Incident(categoryName = "Iluminação", status = IncidentStatus.RESOLVED)
            )
        )
    }
}
