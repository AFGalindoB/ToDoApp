package com.afgalindob.assistantapp.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.ui.theme.AccentSecondary
import com.afgalindob.assistantapp.ui.theme.BackgroundColor
import com.afgalindob.assistantapp.ui.theme.OnAccentSecondary
import com.afgalindob.assistantapp.ui.theme.OnSurfacePrimary
import com.afgalindob.assistantapp.ui.theme.SurfaceVariant
import com.afgalindob.assistantapp.viewmodel.SettingsViewModel

@Composable
fun AccountScreen(
    viewModel: SettingsViewModel,
    onRendered: () -> Unit
){
    val currentName by viewModel.name.collectAsStateWithLifecycle()
    val currentBio by viewModel.bio.collectAsStateWithLifecycle()
    val currentImageUri by viewModel.imageUri.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }

    var tempName by remember(currentName) { mutableStateOf(currentName) }
    var tempBio by remember(currentBio) { mutableStateOf(currentBio) }
    var tempImageUri by remember(currentImageUri) { mutableStateOf(currentImageUri) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                tempImageUri = it.toString()
            }
        }
    )

    LaunchedEffect(Unit) {
        repeat(2) {
            withFrameNanos { }
        }
        onRendered()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        color = BackgroundColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Foto de perfil circular
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable(enabled = isEditing) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = CircleShape,
                    color = AccentSecondary,
                    shadowElevation = 4.dp
                ) {
                    if (tempImageUri != null) {
                        AsyncImage(
                            model = tempImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.account),
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = OnAccentSecondary
                        )
                    }
                }

                // Indicador visual de que se puede editar la foto
                if (isEditing) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = AccentSecondary.copy(alpha = 0.3f),
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = null,
                            modifier = Modifier.padding(6.dp),
                            tint = OnAccentSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre del usuario
            if (isEditing) {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tempBio,
                    onValueChange = { tempBio = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
            } else {
                Text(
                    text = currentName,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentBio,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (isEditing) {
                    // -- BOTON CANCELAR --
                    Button(
                        onClick = {
                            tempImageUri = currentImageUri
                            isEditing = false
                            focusManager.clearFocus()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceVariant.copy(alpha = 0.3f),
                            contentColor = OnSurfacePrimary
                        ),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // -- BOTON GUARDAR --
                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                newName = tempName,
                                newBio = tempBio,
                                newImageUri = tempImageUri
                            )
                            isEditing = false
                            focusManager.clearFocus()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentSecondary,
                            contentColor = OnAccentSecondary
                        ),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(
                            "Guardar",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isEditing = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentSecondary,
                            contentColor = OnAccentSecondary
                        )
                    ) {
                        Icon(painterResource(R.drawable.edit), null)
                        Spacer(Modifier.width(8.dp))
                        Text("Editar Perfil")
                    }
                }
            }
        }
    }
}