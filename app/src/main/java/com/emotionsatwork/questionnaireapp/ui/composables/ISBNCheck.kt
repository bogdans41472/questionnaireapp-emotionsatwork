package com.emotionsatwork.questionnaireapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ISBNCheck(
    hasPassedCheck: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            var text by remember { mutableStateOf("") }
            var openAlertDialog by remember { mutableStateOf(false) }

            Text(
                modifier = Modifier
                    .padding(20.dp),
                text = "Please enter the ISBN at the back of your book"
            )
            OutlinedTextField(value = text, onValueChange = { text = it })

            Button(modifier = Modifier.padding(20.dp),
                onClick = {
                    val isIsbnValid = checkForISBNValidity(text)
                    if (isIsbnValid) {
                        hasPassedCheck.invoke()
                    } else {
                        openAlertDialog = true
                    }
                }) {
                Column {
                    Text(
                        text = "Submit",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
            if (openAlertDialog) {
                MyAlertDialog(
                    onDismissRequest = { openAlertDialog = false },
                    onConfirmation = { openAlertDialog = false },
                    "Invalid ISBN or AISN",
                    "Please input a correct ISBN or AISN number in order to continue"
                )
            }
        }
    }
}

@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

fun checkForISBNValidity(text: String): Boolean {
    return text == "978-3-033-10189-0" || text == "978-3-033-10189-3"
}


@Composable
@Preview
fun ISBNCheckpreview() {
    ISBNCheck {

    }
}