package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.utils.LocationUtils

@Composable
fun IncidentDirections(
    location: GeoLocation,
    deviceLocation: GeoLocation?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val distanceText = if (deviceLocation == null) {
        stringResource(R.string.distance_unknown)
    } else {
        stringResource(R.string.distance_km, LocationUtils.distanceMeters(deviceLocation, location) / 1000f)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text(distanceText) },
            leadingIcon = {
                Icon(
                    Icons.Default.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { LocationUtils.openDirections(context, location) }) {
            Icon(Icons.Default.Directions, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.navigate_to_incident))
        }
    }
}
