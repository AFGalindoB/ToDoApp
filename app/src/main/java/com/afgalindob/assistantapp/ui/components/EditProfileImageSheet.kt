package com.afgalindob.assistantapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.ui.theme.AccentSecondary
import com.afgalindob.assistantapp.ui.theme.BackgroundColor
import com.afgalindob.assistantapp.ui.theme.OnSurfacePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageSheet(
    hasImage: Boolean,
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onEditExisting: () -> Unit,
    onRemoveImage: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundColor, // Mantiene la estética Noir
        contentColor = OnSurfacePrimary,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "Foto de perfil",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Opción: Galería
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPickImage() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.image),
                    contentDescription = null,
                    tint = AccentSecondary
                )
                Spacer(Modifier.width(16.dp))
                Text("Elegir de la galería", style = MaterialTheme.typography.bodyLarge)
            }

            // Opción: Eliminar (Solo si existe una imagen previa)
            if (hasImage) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditExisting() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.edit), null, tint = AccentSecondary)
                    Spacer(Modifier.width(16.dp))
                    Text("Ajustar encuadre actual", style = MaterialTheme.typography.bodyLarge)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRemoveImage() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Quitar foto",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}