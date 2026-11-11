package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import androidx.compose.ui.tooling.preview.Preview
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.User
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.ui.components.PendingCitizenCard
import pt.isec.amov.cityfix.ui.theme.CityFixTheme

@Composable
fun CitizenValidationScreen(
    citizens: List<User>,
    onActivate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        if (citizens.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.validation_empty))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = citizens, key = { it.id }) { citizen ->
                    PendingCitizenCard(citizen = citizen, onActivate = { onActivate(citizen.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CitizenValidationScreenPreview() {
    CityFixTheme {
        CitizenValidationScreen(
            citizens = listOf(
                User(
                    name = "Carla Sousa",
                    email = "carla.sousa@example.com",
                    phone = "913000002",
                    nif = "345678901",
                    role = UserRole.CITIZEN,
                    status = AccountStatus.PENDING,
                    createdAt = 0L,
                    location = GeoLocation(40.2101, -8.4189)
                ),
                User(
                    name = "Diogo Ferreira",
                    email = "diogo.ferreira@example.com",
                    phone = "914000003",
                    nif = "456789012",
                    role = UserRole.CITIZEN,
                    status = AccountStatus.PENDING,
                    createdAt = 0L
                )
            ),
            onActivate = {}
        )
    }
}
