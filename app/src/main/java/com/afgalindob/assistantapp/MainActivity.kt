package com.afgalindob.assistantapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afgalindob.assistantapp.ui.components.LoadingOverlay
import com.afgalindob.assistantapp.ui.theme.AssistantTheme
import com.afgalindob.assistantapp.utils.LanguageUtils
import com.afgalindob.assistantapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val container = (applicationContext as AssistantAplication).container
        val viewModel = MainViewModel(container)

        splashScreen.setKeepOnScreenCondition {
            !viewModel.isAppReady.value
        }

        enableEdgeToEdge()

        setContent {
            val isAppReady by viewModel.isAppReady.collectAsState()

            val langByRepo by container.userRepository.languageData.collectAsState(initial = null)

            // 2. Estado local para el overlay (solo vive en esta instancia)
            var showOverlay by remember { mutableStateOf(false) }

            LaunchedEffect(langByRepo) {
                if (isAppReady && langByRepo != null) {
                    val currentAppLang = AppCompatDelegate.getApplicationLocales().get(0)?.language

                    if (langByRepo != currentAppLang) {
                        // El idioma cambió en el DataStore pero no en la UI
                        showOverlay = true
                        delay(500) // Tiempo para que el usuario vea el giro del icon

                        // Aplicamos el cambio a nivel sistema
                        LanguageUtils.applyAppLanguage(langByRepo!!)
                    }
                }
            }
            AssistantTheme {
                if (isAppReady) {
                    AssistantApp(container)
                } else {
                    Box(Modifier.fillMaxSize())
                }
                LoadingOverlay(isVisible = showOverlay)
            }
        }
    }
}