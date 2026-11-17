package pt.isec.amov.cityfix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.ui.components.FormTextField
import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.viewmodel.LoginUiState

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSecondFactorCodeChange: (String) -> Unit,
    onConfirmSecondFactor: () -> Unit,
    onCancelSecondFactor: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.login_intro), style = MaterialTheme.typography.bodyLarge)
            FormTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = R.string.field_email,
                error = state.emailError,
                keyboardType = KeyboardType.Email
            )
            FormTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = R.string.field_password,
                error = state.passwordError,
                isPassword = true
            )
            state.error?.let {
                Text(
                    text = stringResource(it),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Button(
                onClick = onSignInClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login_submit))
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            TextButton(onClick = onForgotPasswordClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.login_forgot_password))
            }
            TextButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.login_create_account))
            }
        }
    }

    if (state.isSecondFactorRequired) {
        AlertDialog(
            onDismissRequest = onCancelSecondFactor,
            title = { Text(stringResource(R.string.mfa_login_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.mfa_login_text))
                    OutlinedTextField(
                        value = state.secondFactorCode,
                        onValueChange = onSecondFactorCodeChange,
                        label = { Text(stringResource(R.string.mfa_code)) },
                        isError = state.secondFactorError != null,
                        supportingText = { state.secondFactorError?.let { Text(stringResource(it)) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmSecondFactor, enabled = !state.isLoading) {
                    Text(stringResource(R.string.mfa_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelSecondFactor) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (state.isAccountPending || state.isResetEmailSent) {
        AlertDialog(
            onDismissRequest = onDismissMessage,
            title = {
                Text(
                    stringResource(
                        if (state.isAccountPending) R.string.login_pending_title else R.string.login_reset_sent_title
                    )
                )
            },
            text = {
                Text(
                    stringResource(
                        if (state.isAccountPending) R.string.login_pending_text else R.string.login_reset_sent_text
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = onDismissMessage) { Text(stringResource(android.R.string.ok)) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    CityFixTheme {
        LoginScreen(
            state = LoginUiState(email = "bruno.costa@example.com", error = R.string.error_credentials),
            onEmailChange = {},
            onPasswordChange = {},
            onSignInClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            onSecondFactorCodeChange = {},
            onConfirmSecondFactor = {},
            onCancelSecondFactor = {},
            onDismissMessage = {}
        )
    }
}
