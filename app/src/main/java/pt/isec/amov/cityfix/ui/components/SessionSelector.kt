package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.ui.theme.CityFixTheme

@Composable
fun SessionSelector(
    currentRole: UserRole?,
    onProfileSelected: (UserRole?) -> Unit,
    modifier: Modifier = Modifier
) {
    val profiles = listOf(
        null to R.string.session_anonymous,
        UserRole.CITIZEN to R.string.role_citizen,
        UserRole.OPERATOR to R.string.role_operator
    )
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.session_label))
        profiles.forEach { (role, labelRes) ->
            FilterChip(
                selected = currentRole == role,
                onClick = { onProfileSelected(role) },
                label = { Text(stringResource(labelRes)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionSelectorPreview() {
    CityFixTheme {
        SessionSelector(currentRole = UserRole.CITIZEN, onProfileSelected = {})
    }
}
