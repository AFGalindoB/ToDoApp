package com.afgalindob.assistantapp.data.local.preferences

import com.afgalindob.assistantapp.utils.LanguageUtils

data class UserPreferences(
    val name: String = "Usuario",
    val bio: String = "Sin descripción",
    val imageUri: String? = null,
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val zoom: Float = 1f,
    val language: String = LanguageUtils.getSystemLanguageCode()
)