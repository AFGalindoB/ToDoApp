package com.afgalindob.assistantapp.data.repository.user

import com.afgalindob.assistantapp.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userData: Flow<UserPreferences>
    val languageData: Flow<String>
    suspend fun saveUser(userPrefs: UserPreferences)
    suspend fun updateLanguage(languageCode: String)
    suspend fun updateReminderTime(time: String)
}