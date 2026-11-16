package pt.isec.amov.cityfix.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.viewmodel.ProfileUiState

@Composable
fun SecondFactorSection(
    state: ProfileUiState,
    onSendVerificationEmail: () -> Unit,
    onCheckEmailVerified: () -> Unit,
    onStart: () -> Unit,
    onCodeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val secretKey = state.totpSecretKey

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.mfa_title), style = MaterialTheme.typography.titleSmall)
        when {
            state.hasSecondFactor -> Text(stringResource(R.string.mfa_active))
            !state.isEmailVerified -> {
                Text(
                    stringResource(
                        if (state.isVerificationEmailSent) R.string.mfa_verification_sent else R.string.mfa_needs_verified_email
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onSendVerificationEmail) {
                        Text(stringResource(R.string.mfa_send_verification))
                    }
                    TextButton(onClick = onCheckEmailVerified) {
                        Text(stringResource(R.string.mfa_check_verification))
                    }
                }
            }
            secretKey == null -> {
                Text(stringResource(R.string.mfa_description))
                Button(onClick = onStart, enabled = !state.isSecondFactorLoading) {
                    Text(stringResource(R.string.mfa_enable))
                }
            }
            else -> {
                Text(stringResource(R.string.mfa_enroll_instructions))
                SelectionContainer {
                    Text(text = secretKey, fontFamily = FontFamily.Monospace)
                }
                state.totpUri?.let { uri ->
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
                        } catch (_: ActivityNotFoundException) {
                        }
                    }) {
                        Text(stringResource(R.string.mfa_open_app))
                    }
                }
                OutlinedTextField(
                    value = state.totpCode,
                    onValueChange = onCodeChange,
                    label = { Text(stringResource(R.string.mfa_code)) },
                    isError = state.secondFactorError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onConfirm, enabled = !state.isSecondFactorLoading) {
                        Text(stringResource(R.string.mfa_confirm))
                    }
                    TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
                }
            }
        }
        state.secondFactorError?.let {
            Text(
                text = stringResource(it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
