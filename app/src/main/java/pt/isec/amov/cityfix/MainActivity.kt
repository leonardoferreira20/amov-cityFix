package pt.isec.amov.cityfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.isec.amov.cityfix.data.model.GeoPoint
import pt.isec.amov.cityfix.data.model.Incident
import pt.isec.amov.cityfix.data.model.IncidentStatus
import pt.isec.amov.cityfix.data.model.Situation
import pt.isec.amov.cityfix.data.model.Visibility
import pt.isec.amov.cityfix.ui.screens.MainScreen
import pt.isec.amov.cityfix.ui.screens.MainScreenContent
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import kotlin.collections.listOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CityFixTheme {
                MainScreen();
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    CityFixTheme {
        MainScreenContent(
            incidents = listOf(
                Incident(
                    id = "1",
                    authorId = "user001",
                    categoryId = "cat001",
                    categoryName = "Telhado",
                    situation = Situation.DAMAGE,
                    description = "O foguete vai no aire... arrebenta fica todo qemado, mas ningem... hic.... quas meninas da ribera do sado!",
                    justification = "",
                    visibility = Visibility.PUBLIC,
                    location = GeoPoint(40.21, -8.43),
                    status = IncidentStatus.OPEN,
                    media = emptyList(),
                    createdAt = System.currentTimeMillis(),
                    reopen = false
                ),
                Incident(
                    id = "2",
                    authorId = "user002",
                    categoryId = "cat002",
                    categoryName = "Sinal Transito",
                    situation = Situation.DESTRUCTION,
                    description = "Fude-se o poste. Nao tive culpa sr guarda",
                    justification = "",
                    visibility = Visibility.PUBLIC,
                    location = GeoPoint(40.22, -8.44),
                    status = IncidentStatus.OPEN,
                    media = emptyList(),
                    createdAt = System.currentTimeMillis(),
                    reopen = false
                ),
                Incident(
                    id = "3",
                    authorId = "user002",
                    categoryId = "cat002",
                    categoryName = "Caralho em pe",
                    situation = Situation.DESTRUCTION,
                    description = "Fude-se o poste. Nao tive culpa sr guarda",
                    justification = "",
                    visibility = Visibility.PUBLIC,
                    location = GeoPoint(40.22, -8.44),
                    status = IncidentStatus.OPEN,
                    media = emptyList(),
                    createdAt = System.currentTimeMillis(),
                    reopen = false
                ),
                Incident(
                    id = "4",
                    authorId = "user002",
                    categoryId = "cat002",
                    categoryName = "Conas",
                    situation = Situation.DESTRUCTION,
                    description = "Fude-se o poste. Nao tive culpa sr guarda",
                    justification = "",
                    visibility = Visibility.PUBLIC,
                    location = GeoPoint(40.22, -8.44),
                    status = IncidentStatus.OPEN,
                    media = emptyList(),
                    createdAt = System.currentTimeMillis(),
                    reopen = false
                )
            ),
            isExpanded = false,
            onIncidentClick = {}
        )
    }
}