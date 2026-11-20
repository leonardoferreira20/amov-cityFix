package pt.isec.amov.cityfix.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.ui.theme.CityFixPetrol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    @StringRes titleRes: Int,
    isLargeText: Boolean,
    onToggleLargeText: () -> Unit,
    canNavigateBack: Boolean,
    showExpandCards: Boolean,
    areAllCardsExpanded: Boolean,
    showMap: Boolean,
    showCategories: Boolean,
    showValidation: Boolean,
    showLogin: Boolean,
    showProfile: Boolean,
    onNavigateBack: () -> Unit,
    onToggleAllCards: () -> Unit,
    onMapClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onValidationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            LargeTextToggle(isLargeText = isLargeText, onToggle = onToggleLargeText)
            if (showExpandCards) {
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
            if (showMap) {
                IconButton(onClick = onMapClick) {
                    Icon(Icons.Default.Map, contentDescription = stringResource(R.string.map_title))
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
            if (showLogin) {
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.login_action))
                    }
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

@Composable
private fun LargeTextToggle(
    isLargeText: Boolean,
    onToggle: () -> Unit
) {
    val description = stringResource(R.string.large_text_toggle)
    val state = stringResource(if (isLargeText) R.string.large_text_on else R.string.large_text_off)
    IconToggleButton(
        checked = isLargeText,
        onCheckedChange = { onToggle() },
        colors = IconButtonDefaults.iconToggleButtonColors(
            contentColor = Color.White,
            checkedContainerColor = Color.White,
            checkedContentColor = CityFixPetrol
        ),
        modifier = Modifier.semantics {
            contentDescription = description
            stateDescription = state
        }
    ) {
        Text(
            text = stringResource(if (isLargeText) R.string.large_text_label_on else R.string.large_text_label_off),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.clearAndSetSemantics {}
        )
    }
}
