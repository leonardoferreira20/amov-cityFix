package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import java.util.concurrent.TimeUnit

@Composable
fun DashboardStatsCard(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    val averageRepairDays = stats.averageRepairTimeMillis?.let {
        it.toDouble() / TimeUnit.DAYS.toMillis(1)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatItem(
                value = averageRepairDays?.let { stringResource(R.string.stat_days_value, it) }
                    ?: stringResource(R.string.stat_no_value),
                label = stringResource(R.string.stat_average_repair_time),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = stats.totalIncidents.toString(),
                label = stringResource(R.string.stat_total_incidents),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                value = stringResource(R.string.stat_percentage_value, stats.activePercentage),
                label = stringResource(R.string.stat_active_incidents),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardStatsCardPreview() {
    CityFixTheme {
        DashboardStatsCard(
            stats = DashboardStats(
                averageRepairTimeMillis = TimeUnit.DAYS.toMillis(10),
                totalIncidents = 4,
                activePercentage = 75.0
            )
        )
    }
}
