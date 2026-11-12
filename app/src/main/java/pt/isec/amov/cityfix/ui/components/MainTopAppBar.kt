package pt.isec.amov.cityfix.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.ui.theme.CityFixPetrol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    @StringRes titleRes: Int,
    canNavigateBack: Boolean,
    isDashboard: Boolean,
    areAllCardsExpanded: Boolean,
    showCategories: Boolean,
    showValidation: Boolean,
    showRegister: Boolean,
    showProfile: Boolean,
    onNavigateBack: () -> Unit,
    onToggleAllCards: () -> Unit,
    onCategoriesClick: () -> Unit,
    onValidationClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CityFixPetrol,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
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
                IconButton(onClick = onToggleAllCards) {
                    Icon(
                        imageVector = if (areAllCardsExpanded) Icons.Default.ViewAgenda else Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(
                            if (areAllCardsExpanded) R.string.collapse_cards else R.string.expand_cards
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (areAllCardsExpanded) Color.White.copy(alpha = 0.16f) else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(6.dp)
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
            if (showProfile) {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile_title))
                }
            }
        }
    )
}
