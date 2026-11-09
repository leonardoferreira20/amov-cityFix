package pt.isec.amov.cityfix.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.AccountStatus
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.viewmodel.RegisterUiState

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNifChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onImpactDetectionChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onDone: () -> Unit,
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
            RegisterField(state.name, onNameChange, R.string.field_name, state.nameError, KeyboardType.Text)
            RegisterField(
                value = state.email,
                onValueChange = onEmailChange,
                label = R.string.field_email,
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                hint = if (state.role == UserRole.OPERATOR) R.string.register_operator_email_hint else null
            )
            RegisterField(state.phone, onPhoneChange, R.string.field_phone, state.phoneError, KeyboardType.Phone)
            RegisterField(state.nif, onNifChange, R.string.field_nif, state.nifError, KeyboardType.Number)

            Text(stringResource(R.string.register_profile_label), style = MaterialTheme.typography.titleSmall)
            Column(modifier = Modifier.selectableGroup()) {
                UserRole.entries.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = state.role == role,
                                onClick = { onRoleChange(role) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = state.role == role, onClick = null)
                        Text(text = stringResource(roleLabel(role)), modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }

            if (state.role == UserRole.CITIZEN) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = state.impactDetectionEnabled,
                            onValueChange = onImpactDetectionChange,
                            role = Role.Switch
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.register_impact_detection))
                        Text(
                            text = stringResource(R.string.register_impact_detection_hint),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(checked = state.impactDetectionEnabled, onCheckedChange = null)
                }
            }

            Button(
                onClick = onRegisterClick,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.register_submit))
            }
        }
    }

    state.registeredStatus?.let { status ->
        val isPending = status == AccountStatus.PENDING
        AlertDialog(
            onDismissRequest = onDone,
            confirmButton = {
                TextButton(onClick = onDone) { Text(stringResource(android.R.string.ok)) }
            },
            title = {
                Text(
                    stringResource(
                        if (isPending) R.string.register_done_pending_title else R.string.register_done_active_title
                    )
                )
            },
            text = {
                Text(
                    stringResource(
                        if (isPending) R.string.register_done_pending_text else R.string.register_done_active_text
                    )
                )
            }
        )
    }
}

@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    @StringRes error: Int?,
    keyboardType: KeyboardType,
    @StringRes hint: Int? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error != null,
        supportingText = { (error ?: hint)?.let { Text(stringResource(it)) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@StringRes
private fun roleLabel(role: UserRole): Int = when (role) {
    UserRole.CITIZEN -> R.string.role_citizen
    UserRole.OPERATOR -> R.string.role_operator
}
