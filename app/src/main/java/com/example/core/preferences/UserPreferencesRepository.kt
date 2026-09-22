package com.example.core.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hokmchi_preferences")

data class AppSettings(
    val activeThemeId: String = "persian_carpet",
    val activeCardBackId: String = "classic_red",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_ID = stringPreferencesKey("active_theme_id")
        val CARD_BACK_ID = stringPreferencesKey("active_card_back_id")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            activeThemeId = preferences[PreferencesKeys.THEME_ID] ?: "persian_carpet",
            activeCardBackId = preferences[PreferencesKeys.CARD_BACK_ID] ?: "classic_red",
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            animationsEnabled = preferences[PreferencesKeys.ANIMATIONS_ENABLED] ?: true,
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        )
    }

    suspend fun setTheme(themeId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_ID] = themeId
        }
    }

    suspend fun setCardBack(cardBackId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CARD_BACK_ID] = cardBackId
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
