package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AccountScreen(textColor: Color, backgroundColor: Color){
    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Text("Esta es la pantalla de la cuenta", color = textColor)
    }
}