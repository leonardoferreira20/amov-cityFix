package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.ui.components.Dashboard
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Pagina Principal")

        Dashboard(
            incidents = incidents,
            isExpanded = isExpanded,
            onIncidentClick = onIncidentClick,
            modifier = Modifier.weight(1f)
                .background(Color.Blue)
        )
        Box(
            modifier = Modifier.weight(.5f)
                .background(Color.Red)
                .fillMaxSize()
        ){
            Text("Igual ao que está em baixo")
        }
        Box(
            modifier = Modifier.weight(1f)
                .background(Color.Green)
                .fillMaxSize()
        ){
            Text("Qualquer merda")
        }
    }
}