package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.afgalindob.todoapp.ui.theme.BackgroundColor

@Composable
fun AccountScreen(){
    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
        Text("Esta es la pantalla de la cuenta", style = MaterialTheme.typography.headlineMedium)
    }
}