package com.afgalindob.assistantapp.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferencesManager(private val context: Context) {
    companion object {
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val BIO_KEY = stringPreferencesKey("user_bio")
        private val IMAGE_KEY = stringPreferencesKey("user_image_uri")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            name = prefs[NAME_KEY] ?: "Usuario",
            bio = prefs[BIO_KEY] ?: "Sin descripción",
            imageUri = prefs[IMAGE_KEY]
        )
    }

    suspend fun saveUserPreferences(userPrefs: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = userPrefs.name
            prefs[BIO_KEY] = userPrefs.bio
            if (userPrefs.imageUri != null) {
                prefs[IMAGE_KEY] = userPrefs.imageUri
            } else {
                prefs.remove(IMAGE_KEY)
            }
        }
    }
}