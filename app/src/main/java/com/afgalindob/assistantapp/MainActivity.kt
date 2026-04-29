package com.afgalindob.assistantapp

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.afgalindob.assistantapp.utils.AlarmScheduler
import com.afgalindob.assistantapp.utils.LanguageUtils
import com.afgalindob.assistantapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val container = (applicationContext as AssistantAplication).container
        val viewModel = MainViewModel(container)

        splashScreen.setKeepOnScreenCondition { !viewModel.isAppReady.value }

        enableEdgeToEdge()

        setContent {
            val isAppReady by viewModel.isAppReady.collectAsState()
            val langByRepo by container.userRepository.languageData.collectAsState(initial = null)
            val preferences by container.userRepository.userData.collectAsState(initial = null)
            var showOverlay by remember { mutableStateOf(false) }

            LaunchedEffect(langByRepo) {
                if (isAppReady && langByRepo != null) {
                    val currentAppLang = AppCompatDelegate.getApplicationLocales().get(0)?.language
                    if (langByRepo != currentAppLang) {
                        showOverlay = true
                        delay(500)
                        LanguageUtils.applyAppLanguage(langByRepo!!)
                    }
                }
            }

            LaunchedEffect(preferences?.reminderTime) {
                preferences?.reminderTime?.let { time ->
                    Log.d("Main_Alarm", "Sincronizando alarma para las: $time")
                    AlarmScheduler.schedule(applicationContext, time)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}