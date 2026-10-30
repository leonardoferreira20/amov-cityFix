package pt.isec.amov.cityfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.data.model.Incident

@Composable
fun Dashboard(
    incidents: List<Incident>,
    isExpanded: Boolean,
    onIncidentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxSize()
    ) {
        item{
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillParentMaxWidth(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.18f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lista de incidentes mais recentes")
                }

                IncidentList(
                    incidents = incidents,
                    isExpanded = isExpanded,
                    onIncidentClick = onIncidentClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.82f)
                )
            }
        }

        item{
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillParentMaxWidth(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.18f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Graphics")
                }

                IncidentList(
                    incidents = incidents,
                    isExpanded = isExpanded,
                    onIncidentClick = onIncidentClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.82f)
                )
            }
        }
    }
}