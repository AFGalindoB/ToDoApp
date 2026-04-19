package com.afgalindob.assistantapp.data.repository.user

import com.afgalindob.assistantapp.data.local.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userData: Flow<UserPreferences>
    suspend fun saveUser(userPrefs: UserPreferences)
}