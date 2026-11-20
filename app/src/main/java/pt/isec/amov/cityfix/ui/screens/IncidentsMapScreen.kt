package pt.isec.amov.cityfix.ui.screens

import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
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
    val cityCamera = remember {
        CameraPosition.fromLatLngZoom(LatLng(CITY_CENTER.latitude, CITY_CENTER.longitude), CITY_ZOOM)
    }
    val cameraState = rememberCameraPositionState { position = cityCamera }
    val pinPositions = remember(incidents) {
        incidents.map { LatLng(it.location.latitude, it.location.longitude) }.toSet()
    }
    var mapSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize().onSizeChanged { mapSize = it },
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
            LaunchedEffect(exact, pinPositions, mapSize) {
                when {
                    !exact || pinPositions.isEmpty() -> cameraState.position = cityCamera
                    pinPositions.size == 1 -> {
                        cameraState.position = CameraPosition.fromLatLngZoom(pinPositions.first(), SINGLE_ZOOM)
                    }
                    mapSize.width > 0 && mapSize.height > 0 -> {
                        val bounds = LatLngBounds.builder().apply {
                            pinPositions.forEach { include(it) }
                        }.build()
                        val padding = minOf(BOUNDS_PADDING, minOf(mapSize.width, mapSize.height) / 4)
                        cameraState.move(
                            CameraUpdateFactory.newLatLngBounds(bounds, mapSize.width, mapSize.height, padding)
                        )
                    }
                }
            }
            incidents.forEach { incident ->
                key(incident.id) {
                    IncidentMarker(incident = incident, onOpenIncident = onOpenIncident)
                }
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

@Preview(showBackground = true)
@Composable
private fun IncidentsMapScreenPreview() {
    CityFixTheme {
        IncidentsMapScreen(incidents = emptyList(), exact = false, onOpenIncident = {})
    }
}
