package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.ui.components.IncidentList


@Composable
fun MainScreen() {
    MainScreenContent(
        incidents = emptyList(),
        isExpanded = false,
        onIncidentClick = {}
    )
}

@Composable
fun MainScreenContent(
    incidents: List<Incident>,
    isExpanded: Boolean,
    onIncidentClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        IncidentList(
            incidents = incidents,
            isExpanded = isExpanded,
            onIncidentClick = onIncidentClick
        )
    }
}