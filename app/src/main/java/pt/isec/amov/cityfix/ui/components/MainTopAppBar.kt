package pt.isec.amov.cityfix.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pt.isec.amov.cityfix.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    @StringRes titleRes: Int,
    canNavigateBack: Boolean,
    isDashboard: Boolean,
    isExpanded: Boolean,
    showCategories: Boolean,
    showValidation: Boolean,
    showRegister: Boolean,
    onNavigateBack: () -> Unit,
    onToggleExpand: () -> Unit,
    onCategoriesClick: () -> Unit,
    onValidationClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(titleRes)) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back)
                    )
                }
            }
        },
        actions = {
            if (isDashboard) {
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ViewAgenda else Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.toggle_cards)
                    )
                }
            }
            if (showCategories) {
                IconButton(onClick = onCategoriesClick) {
                    Icon(Icons.Default.Category, contentDescription = stringResource(R.string.categories_title))
                }
            }
            if (showValidation) {
                IconButton(onClick = onValidationClick) {
                    Icon(Icons.Default.HowToReg, contentDescription = stringResource(R.string.validation_title))
                }
            }
            if (showRegister) {
                IconButton(onClick = onRegisterClick) {
                    Icon(Icons.Default.PersonAdd, contentDescription = stringResource(R.string.register_title))
                }
            }
        }
    )
}
