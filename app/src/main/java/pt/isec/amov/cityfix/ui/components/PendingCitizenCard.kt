package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.User
import java.util.Locale

@Composable
fun PendingCitizenCard(
    citizen: User,
    onActivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val location = citizen.location?.let {
        String.format(Locale.ROOT, "%.5f, %.5f", it.latitude, it.longitude)
    }
    val activateDescription = stringResource(R.string.validation_activate_description, citizen.name)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = citizen.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.validation_nif, citizen.nif),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (location != null) {
                    stringResource(R.string.validation_location, location)
                } else {
                    stringResource(R.string.validation_location_unknown)
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onActivate,
                modifier = Modifier
                    .align(Alignment.End)
                    .semantics { contentDescription = activateDescription }
            ) {
                Text(stringResource(R.string.validation_activate))
            }
        }
    }
}
