package com.afgalindob.assistantapp.data.repository.user

import com.afgalindob.assistantapp.data.local.preferences.UserPreferencesManager
import com.afgalindob.assistantapp.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(
    private val preferencesManager: UserPreferencesManager
) : UserRepository {

    override val userData: Flow<UserPreferences> = preferencesManager.userPreferencesFlow
    override val languageData: Flow<String> = preferencesManager.languageFlow

    override suspend fun saveUser(userPrefs: UserPreferences) {
        preferencesManager.saveUserPreferences(userPrefs)
    }

    override suspend fun updateLanguage(languageCode: String) {
        preferencesManager.updateLanguage(languageCode)
    }

    override suspend fun updateReminderTime(time: String) {
        preferencesManager.updateReminderTime(time)
    }
}