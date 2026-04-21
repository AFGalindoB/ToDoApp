package com.afgalindob.assistantapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afgalindob.assistantapp.ui.theme.AssistantTheme
import com.afgalindob.assistantapp.viewmodel.MainViewModel

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
            AssistantTheme {
                if (isAppReady) {
                    AssistantApp(container, true)
                } else {
                    Box(Modifier.fillMaxSize())
                }
            }
        }
    }
}