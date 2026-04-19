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

    // Observamos el Flow del repositorio y lo convertimos en estados de UI
    private val _userPreferences = userRepository.userData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences()
    )

    val name: StateFlow<String> = _userPreferences
        .map { it.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Cargando...")

    val bio: StateFlow<String> = _userPreferences
        .map { it.bio }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Cargando...")

    val imageUri: StateFlow<String?> = _userPreferences.map { it.imageUri }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Recibe strings crudos de la UI y los empaqueta como UserPreferences para el Repo
    fun updateProfile(newName: String, newBio: String, newImageUri: String?) {
        viewModelScope.launch {
            userRepository.saveUser(
                UserPreferences(
                    name = newName,
                    bio = newBio,
                    imageUri = newImageUri
                )
            )
        }
    }
}