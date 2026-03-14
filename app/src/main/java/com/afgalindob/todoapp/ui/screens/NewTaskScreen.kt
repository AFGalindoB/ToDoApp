package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.schema.TaskSchema

@Composable
fun NewTaskScreen(
    onCreateTask: (Map<String,String>) -> Unit,
){
    val values = remember {
        mutableStateMapOf<String, String>()
    }

    Column (
        modifier = Modifier
            .padding(40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.new_task_title),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )

        TaskSchema.fields.forEach { field ->

            field.type.RenderInput(
                field = field,
                values = values
            )

            Spacer(modifier = Modifier.height(16.dp))

        }

        Button(onClick = {
            onCreateTask(values.toMap())
            values.clear()
        } ) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.add_task),
                    contentDescription = "Create Task"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_task))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewTaskScreenPreview(){
    NewTaskScreen(onCreateTask = {})
}