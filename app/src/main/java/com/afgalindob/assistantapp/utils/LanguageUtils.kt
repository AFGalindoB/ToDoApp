package com.afgalindob.assistantapp.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageUtils {

    /**
     * Obtiene el código de idioma del sistema y lo normaliza a "es" o "en".
     * Si el idioma del sistema no es ninguno de los dos, devuelve "en" por defecto.
     */
    fun getSystemLanguageCode(): String {
        val currentLang = Locale.getDefault().language.lowercase()
        return when {
            currentLang.startsWith("es") -> "es"
            currentLang.startsWith("en") -> "en"
            else -> "en" // Fallback para Noir Assistant
        }
    }

    /**
     * Valida un código de entrada para asegurar que solo manejemos los soportados.
     */
    fun normalizeLanguageCode(code: String): String {
        return when (code.lowercase().take(2)) {
            "es" -> "es"
            "en" -> "en"
            else -> "en"
        }
    }

    fun applyAppLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(
            normalizeLanguageCode(languageCode)
        )
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}