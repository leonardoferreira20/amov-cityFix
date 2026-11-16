package pt.isec.amov.cityfix.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pt.isec.amov.cityfix.R

@Composable
fun BackgroundLocationDialog(
    isOperator: Boolean,
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.background_location_title)) },
        text = {
            Text(
                stringResource(
                    if (isOperator) R.string.background_location_operator else R.string.background_location_impacts
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onAllow) { Text(stringResource(R.string.background_location_allow)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.not_now)) }
        }
    )
}
