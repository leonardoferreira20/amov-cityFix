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
fun SecondaryScreen() {
    SecondaryScreenContent(

    )
}

@Composable
fun SecondaryScreenContent(

    ) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Yellow)
    ){
        Text("IKA")
    }
}