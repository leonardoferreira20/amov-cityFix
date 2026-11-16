package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.isec.amov.cityfix.data.model.GeoLocation

private const val EXACT_ZOOM = 17f
private const val PUBLIC_ZOOM = 11f
private const val PICKER_ZOOM = 15f

@Composable
fun IncidentMap(
    location: GeoLocation,
    exact: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        MapPlaceholder(modifier)
        return
    }
    val position = LatLng(location.latitude, location.longitude)
    val zoom = if (exact) EXACT_ZOOM else PUBLIC_ZOOM
    val cameraState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, zoom)
    }
    LaunchedEffect(position, zoom) {
        cameraState.position = CameraPosition.fromLatLngZoom(position, zoom)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraState,
        contentDescription = contentDescription,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = exact,
            zoomGesturesEnabled = exact,
            scrollGesturesEnabled = exact,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        val markerState = remember(position) { MarkerState(position = position) }
        Marker(state = markerState)
    }
}

@Composable
fun MapLocationPicker(
    selected: GeoLocation?,
    initialCenter: GeoLocation,
    contentDescription: String,
    onLocationSelected: (GeoLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        MapPlaceholder(modifier)
        return
    }
    val center = selected ?: initialCenter
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(center.latitude, center.longitude), PICKER_ZOOM)
    }
    var selectedPin by remember {
        mutableStateOf(selected?.let { LatLng(it.latitude, it.longitude) })
    }
    LaunchedEffect(selected) {
        selected?.let {
            val pin = LatLng(it.latitude, it.longitude)
            if (pin != selectedPin) {
                selectedPin = pin
                cameraState.position = CameraPosition.fromLatLngZoom(pin, PICKER_ZOOM)
            }
        }
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraState,
        contentDescription = contentDescription,
        uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
        onMapClick = { point ->
            selectedPin = point
            onLocationSelected(GeoLocation(point.latitude, point.longitude))
        }
    ) {
        selectedPin?.let { pin ->
            val markerState = remember(pin) { MarkerState(position = pin) }
            Marker(state = markerState)
        }
    }
}

@Composable
private fun MapPlaceholder(modifier: Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
}
