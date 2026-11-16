package pt.isec.amov.cityfix.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.catch
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.utils.LocationUtils

@Composable
fun rememberDeviceLocation(hasPermission: Boolean): State<GeoLocation?> {
    val context = LocalContext.current
    return produceState<GeoLocation?>(initialValue = null, hasPermission) {
        if (!hasPermission) return@produceState
        LocationUtils.locationUpdates(context)
            .catch { }
            .collect { value = it }
    }
}
