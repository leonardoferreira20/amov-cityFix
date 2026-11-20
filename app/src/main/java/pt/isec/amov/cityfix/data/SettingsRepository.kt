package pt.isec.amov.cityfix.data

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(context: Context) {
    private val preferences = context.getSharedPreferences("cityfix_settings", Context.MODE_PRIVATE)

    fun isLargeTextEnabled(): Boolean = preferences.getBoolean(KEY_LARGE_TEXT, false)

    fun setLargeTextEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(KEY_LARGE_TEXT, enabled) }
    }

    private companion object {
        const val KEY_LARGE_TEXT = "large_text"
    }
}
