package com.afgalindob.assistantapp.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
        private val OFFSET_X_KEY = floatPreferencesKey("user_image_offset_x")
        private val OFFSET_Y_KEY = floatPreferencesKey("user_image_offset_y")
        private val ZOOM_KEY = floatPreferencesKey("user_image_zoom")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            name = prefs[NAME_KEY] ?: "Usuario",
            bio = prefs[BIO_KEY] ?: "Sin descripción",
            imageUri = prefs[IMAGE_KEY],
            centerX = prefs[OFFSET_X_KEY] ?: 0f,
            centerY = prefs[OFFSET_Y_KEY] ?: 0f,
            zoom = prefs[ZOOM_KEY] ?: 1f
        )
    }

    suspend fun saveUserPreferences(userPrefs: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = userPrefs.name
            prefs[BIO_KEY] = userPrefs.bio
            prefs[OFFSET_X_KEY] = userPrefs.centerX
            prefs[OFFSET_Y_KEY] = userPrefs.centerY
            prefs[ZOOM_KEY] = userPrefs.zoom

            if (userPrefs.imageUri != null) {
                prefs[IMAGE_KEY] = userPrefs.imageUri
            } else {
                prefs.remove(IMAGE_KEY)
            }
        }
    }
}