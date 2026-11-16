package pt.isec.amov.cityfix.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.DashboardStats
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.ui.components.Dashboard
import pt.isec.amov.cityfix.ui.components.DashboardStatsCard
import pt.isec.amov.cityfix.ui.components.IncidentFilter
import pt.isec.amov.cityfix.utils.AnonymousAccess
import pt.isec.amov.cityfix.utils.canCreateIncident
import pt.isec.amov.cityfix.viewmodel.IncidentsViewModel

@Composable
fun MainScreen(
    viewModel: IncidentsViewModel,
    currentUser: User?,
    incidents: List<Incident>,
    onLoginClick: () -> Unit,
    anonymousAccess: AnonymousAccess,
    onRequestLocationPermission: () -> Unit,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    onCreateIncident: () -> Unit
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val onlyMine by viewModel.onlyMine.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser) {
        viewModel.setCurrentUser(currentUser)
    }

    MainScreenContent(
        stats = stats,
        incidents = incidents,
        currentRole = currentUser?.role,
        anonymousAccess = anonymousAccess,
        onRequestLocationPermission = onRequestLocationPermission,
        onLoginClick = onLoginClick,
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
    anonymousAccess: AnonymousAccess,
    onRequestLocationPermission: () -> Unit,
    onLoginClick: () -> Unit,
    onlyMine: Boolean,
    onOnlyMineChange: (Boolean) -> Unit,
    expandedIncidentIds: List<String>,
    onToggleIncident: (String) -> Unit,
    onOpenIncident: (String) -> Unit,
    showCreateIncident: Boolean,
    onCreateIncident: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isAllowed = anonymousAccess == AnonymousAccess.ALLOWED

    val headerContent: @Composable (Modifier) -> Unit = { headerModifier ->
        Column(modifier = headerModifier) {
            if (currentRole == UserRole.CITIZEN) {
                IncidentFilter(onlyMine = onlyMine, onOnlyMineChange = onOnlyMineChange)
            }
            if (isAllowed) {
                DashboardStatsCard(
                    stats = stats,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    val bodyContent: @Composable (Modifier) -> Unit = { bodyModifier ->
        if (isAllowed) {
            Dashboard(
                incidents = incidents,
                expandedIncidentIds = expandedIncidentIds,
                onToggleIncident = onToggleIncident,
                onOpenIncident = onOpenIncident,
                modifier = bodyModifier
            )
        } else {
            AnonymousRestriction(
                access = anonymousAccess,
                onRequestLocationPermission = onRequestLocationPermission,
                onLoginClick = onLoginClick,
                modifier = bodyModifier
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            if (showCreateIncident) {
                FloatingActionButton(onClick = onCreateIncident) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.report_title))
                }
            }
        }
    ) { innerPadding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                headerContent(
                    Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
                bodyContent(
                    Modifier
                        .weight(3f)
                        .fillMaxHeight()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                headerContent(Modifier.fillMaxWidth())
                bodyContent(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AnonymousRestriction(
    access: AnonymousAccess,
    onRequestLocationPermission: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (access == AnonymousAccess.LOCATING) {
            CircularProgressIndicator()
        } else {
            Icon(Icons.Default.LocationOff, contentDescription = null)
        }
        Text(
            text = stringResource(
                when (access) {
                    AnonymousAccess.LOCATING -> R.string.anonymous_locating
                    AnonymousAccess.NO_PERMISSION -> R.string.anonymous_no_permission
                    else -> R.string.anonymous_out_of_area
                }
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (access == AnonymousAccess.NO_PERMISSION) {
            Button(onClick = onRequestLocationPermission) {
                Text(stringResource(R.string.location_permission_allow))
            }
        }
        if (access != AnonymousAccess.LOCATING) {
            OutlinedButton(onClick = onLoginClick) {
                Text(stringResource(R.string.login_title))
            }
        }
    }
}
