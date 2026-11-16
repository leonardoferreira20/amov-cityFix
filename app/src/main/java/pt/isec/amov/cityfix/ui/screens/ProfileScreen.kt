package pt.isec.amov.cityfix.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import pt.isec.amov.cityfix.ui.components.SecondFactorSection
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
    onSendVerificationEmail: () -> Unit,
    onCheckEmailVerified: () -> Unit,
    onStartSecondFactor: () -> Unit,
    onTotpCodeChange: (String) -> Unit,
    onConfirmSecondFactor: () -> Unit,
    onCancelSecondFactor: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.imePadding()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileSection(title = R.string.profile_edit_details) {
                UserPhotoField(name = state.name, picturePath = state.picturePath, onPictureChange = onPictureChange)
                if (state.picturePath != null) {
                    TextButton(onClick = { onPictureChange(null) }) {
                        Text(stringResource(R.string.profile_remove_photo))
                    }
                }
                FormTextField(
                    value = state.phone,
                    onValueChange = onPhoneChange,
                    label = R.string.field_phone,
                    error = state.phoneError,
                    keyboardType = KeyboardType.Phone,
                    enabled = !state.isSaving
                )
                if (state.role == UserRole.CITIZEN) {
                    ImpactDetectionSwitch(
                        checked = state.impactDetectionEnabled,
                        onCheckedChange = onImpactDetectionChange
                    )
                }
                Button(
                    onClick = onSaveClick,
                    enabled = state.isValid && !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(if (state.isSaving) R.string.profile_saving else R.string.profile_save))
                }
            }

            ProfileSection(title = R.string.profile_account_details) {
                ReadOnlyProfileValue(R.string.field_name, state.name)
                ReadOnlyProfileValue(R.string.field_email, state.email)
                ReadOnlyProfileValue(R.string.field_nif, state.nif)
                if (state.role == UserRole.CITIZEN) {
                    ReadOnlyProfileValue(
                        R.string.field_location,
                        state.location?.toDisplayText() ?: stringResource(R.string.field_location_not_set)
                    )
                }
            }

            SecondFactorSection(
                state = state,
                onSendVerificationEmail = onSendVerificationEmail,
                onCheckEmailVerified = onCheckEmailVerified,
                onStart = onStartSecondFactor,
                onCodeChange = onTotpCodeChange,
                onConfirm = onConfirmSecondFactor,
                onCancel = onCancelSecondFactor
            )

            OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.profile_sign_out))
            }
        }
    }
}

@Composable
private fun ProfileSection(@StringRes title: Int, content: @Composable () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ReadOnlyProfileValue(@StringRes label: Int, value: String) {
    Column {
        Text(stringResource(label), style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
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
            onSaveClick = {},
            onSendVerificationEmail = {},
            onCheckEmailVerified = {},
            onStartSecondFactor = {},
            onTotpCodeChange = {},
            onConfirmSecondFactor = {},
            onCancelSecondFactor = {},
            onSignOut = {}
        )
    }
}
