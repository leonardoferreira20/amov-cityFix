package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.GeoLocation
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.ui.components.FormTextField
import pt.isec.amov.cityfix.ui.components.ImpactDetectionSwitch
import pt.isec.amov.cityfix.ui.components.UserPhotoField
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.utils.toDisplayText
import pt.isec.amov.cityfix.viewmodel.ProfileUiState

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onPhoneChange: (String) -> Unit,
    onImpactDetectionChange: (Boolean) -> Unit,
    onPictureChange: (String?) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.imePadding()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserPhotoField(name = state.name, picturePath = state.picturePath, onPictureChange = onPictureChange)

            FormTextField(value = state.name, onValueChange = {}, label = R.string.field_name, enabled = false)
            FormTextField(value = state.email, onValueChange = {}, label = R.string.field_email, enabled = false)
            FormTextField(
                value = state.phone,
                onValueChange = onPhoneChange,
                label = R.string.field_phone,
                error = state.phoneError,
                keyboardType = KeyboardType.Phone
            )
            FormTextField(value = state.nif, onValueChange = {}, label = R.string.field_nif, enabled = false)

            if (state.role == UserRole.CITIZEN) {
                FormTextField(
                    value = state.location?.toDisplayText() ?: stringResource(R.string.field_location_not_set),
                    onValueChange = {},
                    label = R.string.field_location,
                    enabled = false
                )
                ImpactDetectionSwitch(
                    checked = state.impactDetectionEnabled,
                    onCheckedChange = onImpactDetectionChange
                )
            }

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_save))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    CityFixTheme {
        ProfileScreen(
            state = ProfileUiState(
                name = "Bruno Costa",
                email = "bruno.costa@example.com",
                nif = "234567890",
                role = UserRole.CITIZEN,
                location = GeoLocation(40.2086, -8.4207),
                phone = "91200000",
                impactDetectionEnabled = true,
                phoneError = R.string.error_phone_invalid
            ),
            onPhoneChange = {},
            onImpactDetectionChange = {},
            onPictureChange = {},
            onSaveClick = {}
        )
    }
}
