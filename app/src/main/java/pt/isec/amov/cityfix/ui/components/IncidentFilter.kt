package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.ui.theme.CityFixTheme

@Composable
fun IncidentFilter(
    onlyMine: Boolean,
    onOnlyMineChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = !onlyMine,
            onClick = { onOnlyMineChange(false) },
            label = { Text(stringResource(R.string.filter_all)) }
        )
        FilterChip(
            selected = onlyMine,
            onClick = { onOnlyMineChange(true) },
            label = { Text(stringResource(R.string.filter_mine)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncidentFilterPreview() {
    CityFixTheme {
        IncidentFilter(onlyMine = false, onOnlyMineChange = {})
    }
}
