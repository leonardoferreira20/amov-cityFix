package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R

@Composable
fun DevMenuScreen(
    onCategoriesClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onValidationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onCategoriesClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.categories_title))
            }
            Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.register_title))
            }
            Button(onClick = onValidationClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.validation_title))
            }
        }
    }
}
