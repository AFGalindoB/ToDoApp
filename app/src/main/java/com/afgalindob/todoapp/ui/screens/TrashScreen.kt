package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.afgalindob.todoapp.ui.theme.BackgroundColor

@Composable
fun TrashScreen(
    onRendered: () -> Unit
){
    LaunchedEffect(Unit) {
        repeat(2) {
            withFrameNanos { }
        }
        onRendered()
    }
    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
        Text("Esta es la pantalla de la papelera", style = MaterialTheme.typography.headlineMedium)
    }
}