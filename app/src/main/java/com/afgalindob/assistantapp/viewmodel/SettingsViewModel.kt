package com.afgalindob.assistantapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.assistantapp.data.local.preferences.UserPreferences
import com.afgalindob.assistantapp.data.repository.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // Exponemos el estado completo del objeto de dominio
    val userPreferences: StateFlow<UserPreferences> = userRepository.userData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences()
    )

    fun updateProfile(preferences: UserPreferences) {
        viewModelScope.launch {
            userRepository.saveUser(preferences)
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            userRepository.updateLanguage(languageCode)
        }
    }
}