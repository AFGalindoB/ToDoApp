package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    showCompleted: Boolean,
    onCompletedChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(Modifier.width(5.dp))
                Checkbox(
                    checked = showCompleted,
                    onCheckedChange = onCompletedChanged
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = stringResource(R.string.show_completed_option)
                )
            }
            Spacer(Modifier.height(60.dp))

        }
    }
}