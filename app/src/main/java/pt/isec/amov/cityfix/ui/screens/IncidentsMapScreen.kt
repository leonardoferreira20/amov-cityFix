package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.ui.components.labelRes
import pt.isec.amov.cityfix.utils.CITY_CENTER

private const val CITY_ZOOM = 12f
private const val SINGLE_ZOOM = 16f
private const val BOUNDS_PADDING = 120

@Composable
fun IncidentsMapScreen(
    incidents: List<Incident>,
    exact: Boolean,
    onOpenIncident: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
        return
    }
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(CITY_CENTER.latitude, CITY_CENTER.longitude), CITY_ZOOM)
    }

    LaunchedEffect(incidents, exact) {
        if (!exact) return@LaunchedEffect
        when (incidents.size) {
            0 -> Unit
            1 -> {
                val location = incidents.first().location
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), SINGLE_ZOOM)
                )
            }
            else -> {
                val bounds = LatLngBounds.builder().apply {
                    incidents.forEach { include(LatLng(it.location.latitude, it.location.longitude)) }
                }.build()
                cameraState.animate(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING))
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            contentDescription = stringResource(R.string.map_all_description),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = exact,
                zoomGesturesEnabled = exact,
                scrollGesturesEnabled = exact,
                rotationGesturesEnabled = false,
                tiltGesturesEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            incidents.forEach { incident ->
                IncidentMarker(incident = incident, onOpenIncident = onOpenIncident)
            }
        }

        if (incidents.isEmpty()) {
            Text(
                text = stringResource(R.string.no_incidents),
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun IncidentMarker(
    incident: Incident,
    onOpenIncident: (String) -> Unit
) {
    val position = LatLng(incident.location.latitude, incident.location.longitude)
    val markerState = remember(incident.id, position) { MarkerState(position = position) }
    Marker(
        state = markerState,
        title = incident.categoryName,
        snippet = stringResource(
            R.string.map_marker_snippet,
            stringResource(incident.situation.labelRes()),
            stringResource(incident.status.labelRes())
        ),
        onInfoWindowClick = { onOpenIncident(incident.id) }
    )
}
