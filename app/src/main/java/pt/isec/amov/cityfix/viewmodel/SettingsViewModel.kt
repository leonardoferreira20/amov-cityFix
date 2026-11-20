package pt.isec.amov.cityfix.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pt.isec.amov.cityfix.data.SettingsRepository

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _isLargeText = MutableStateFlow(repository.isLargeTextEnabled())
    val isLargeText: StateFlow<Boolean> = _isLargeText.asStateFlow()

    fun toggleLargeText() {
        val enabled = !_isLargeText.value
        repository.setLargeTextEnabled(enabled)
        _isLargeText.value = enabled
    }
}
