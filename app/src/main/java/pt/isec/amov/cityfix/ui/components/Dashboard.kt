package pt.isec.amov.cityfix.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun Dashboard(
    stats: DashboardStats,
    incidents: List<Incident>,
    showOwnershipFilter: Boolean,
    onlyMine: Boolean,
    onOnlyMineChange: (Boolean) -> Unit,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    onSignOut: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val overview: @Composable (Boolean) -> Unit = { showListButton ->
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (onSignOut != null) {
                TextButton(onClick = onSignOut, modifier = Modifier.align(Alignment.End)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.profile_sign_out))
                    }
                }
            }
            DashboardStatsCard(stats)
            if (showOwnershipFilter) {
                IncidentFilter(onlyMine = onlyMine, onOnlyMineChange = onOnlyMineChange)
            }
            if (showListButton && incidents.isNotEmpty()) {
                OutlinedButton(
                    onClick = { scope.launch { listState.animateScrollToItem(1) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.dashboard_view_incidents))
                }
            }
            IncidentCharts(incidents = incidents)
        }
    }

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                overview(false)
            }
            IncidentList(
                incidents = incidents,
                expandedIncidentIds = expandedIncidentIds,
                onToggleIncident = onToggleIncident,
                onOpenIncident = onOpenIncident,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    } else {
        IncidentList(
            incidents = incidents,
            expandedIncidentIds = expandedIncidentIds,
            onToggleIncident = onToggleIncident,
            onOpenIncident = onOpenIncident,
            modifier = modifier.fillMaxSize(),
            listState = listState,
            leadingContent = { overview(true) }
        )
    }
}
