package com.afgalindob.assistantapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.afgalindob.assistantapp.ui.theme.BackgroundColor

@Composable
fun AccountScreen(
    onRendered: () -> Unit
){
    LaunchedEffect(Unit) {
        repeat(2) {
            withFrameNanos { }
        }
        onRendered()
    }
    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
        Text("Esta es la pantalla de la cuenta", style = MaterialTheme.typography.headlineMedium)
    }
}