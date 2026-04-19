package com.afgalindob.assistantapp.data.repository.user

import com.afgalindob.assistantapp.data.local.preferences.UserPreferencesManager
import com.afgalindob.assistantapp.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(
    private val preferencesManager: UserPreferencesManager
) : UserRepository {

    override val userData: Flow<UserPreferences> = preferencesManager.userPreferencesFlow

    override suspend fun saveUser(userPrefs: UserPreferences) {
        preferencesManager.saveUserPreferences(userPrefs)
    }
}