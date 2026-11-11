package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.ui.components.Dashboard
import pt.isec.amov.cityfix.ui.components.IncidentFilter
import pt.isec.amov.cityfix.ui.components.SessionSelector
import pt.isec.amov.cityfix.utils.canCreateIncident
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModel
import pt.isec.amov.cityfix.viewmodel.SessionViewModel

@Composable
fun MainScreen(
    viewModel: IncidentsViewModel,
    sessionViewModel: SessionViewModel,
    incidents: List<Incident>,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    onCreateIncident: () -> Unit
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val onlyMine by viewModel.onlyMine.collectAsStateWithLifecycle()
    val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser) {
        viewModel.setCurrentUser(currentUser)
    }

    MainScreenContent(
        stats = stats,
        incidents = incidents,
        currentRole = currentUser?.role,
        onProfileSelected = sessionViewModel::selectProfile,
        onlyMine = onlyMine,
        onOnlyMineChange = viewModel::setOnlyMine,
        expandedIncidentIds = expandedIncidentIds,
        onToggleIncident = onToggleIncident,
        onOpenIncident = onOpenIncident,
        showCreateIncident = canCreateIncident(currentUser),
        onCreateIncident = onCreateIncident
    )
}

@Composable
fun MainScreenContent(
    stats: DashboardStats,
    incidents: List<Incident>,
    currentRole: UserRole?,
    onProfileSelected: (UserRole?) -> Unit,
    onlyMine: Boolean,
    onOnlyMineChange: (Boolean) -> Unit,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    showCreateIncident: Boolean,
    onCreateIncident: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            if (showCreateIncident) {
                FloatingActionButton(onClick = onCreateIncident) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.report_title))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SessionSelector(
                currentRole = currentRole,
                onProfileSelected = onProfileSelected
            )

            if (currentRole == UserRole.CITIZEN) {
                IncidentFilter(onlyMine = onlyMine, onOnlyMineChange = onOnlyMineChange)
            }

            Dashboard(
                stats = stats,
                incidents = incidents,
                expandedIncidentIds = expandedIncidentIds,
                onToggleIncident = onToggleIncident,
                onOpenIncident = onOpenIncident,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
