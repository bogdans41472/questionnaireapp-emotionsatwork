package com.emotionsatwork.questionnaireapp.ui.composables

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.emotionsatwork.questionnaireapp.R

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun Login(
    sharedPreferences: SharedPreferences,
    onLoginComplete: (Boolean) -> Unit
) {
    if (hasInputSerialNumber(sharedPreferences)) {
        onLoginComplete.invoke(true)
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F3F3))
        ) {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val painter = painterResource(id = R.drawable.emotions_at_work)
                Image(
                    painter = painter,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    alignment = Alignment.TopCenter
                )
                var serialNumberHint by remember {
                    mutableStateOf(
                        TextFieldValue(
                            ""
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    val hint = "Enter your book serial number below"
                    val keyboardController = LocalSoftwareKeyboardController.current
                    Column {
                        val textColor = Color.Unspecified.takeOrElse {
                            LocalTextStyle.current.color.takeOrElse {
                                LocalContentColor.current
                            }
                        }
                        // NOTE(text-perf-review): It might be worthwhile writing a bespoke merge implementation that
                        // will avoid reallocating if all of the options here are the defaults
                        val mergedStyle = LocalTextStyle.current.merge(
                            TextStyle(
                                color = textColor
                            )
                        )
                        BasicText(
                            text = hint,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = mergedStyle,
                            onTextLayout = {},
                        )
                        TextField(
                            value = serialNumberHint,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            onValueChange = { serialNumberHint = it },
                            maxLines = 1,
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Normal
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    saveThatUserHasAuthenticated(sharedPreferences)
                                    onLoginComplete.invoke(true)
                                    keyboardController?.hide()
                                })
                        )
                    }
                }
                Button(
                    onClick = {
                        if (serialNumberHint.text == "admin") {
                            saveThatUserHasAuthenticated(sharedPreferences)
                            onLoginComplete.invoke(true)
                        }
                    },
                    elevation = ButtonDefaults.buttonElevation(5.dp)
                ) {
                    val textColor = Color.Unspecified.takeOrElse {
                        LocalTextStyle.current.color.takeOrElse {
                            LocalContentColor.current
                        }
                    }
                    // NOTE(text-perf-review): It might be worthwhile writing a bespoke merge implementation that
                    // will avoid reallocating if all of the options here are the defaults
                    val mergedStyle = LocalTextStyle.current.merge(
                        TextStyle(
                            color = textColor
                        )
                    )
                    BasicText(
                        text = "Login",
                        modifier = Modifier.padding(16.dp),
                        style = mergedStyle,
                        onTextLayout = {},
                    )
                }
            }
        }
    }
}


fun hasInputSerialNumber(sharedPreferences: SharedPreferences): Boolean {
    return sharedPreferences.getBoolean(SERIAL_NUMBER_KEY, false)
}

fun saveThatUserHasAuthenticated(sharedPreferences: SharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(SERIAL_NUMBER_KEY, true)
        .apply()
}

private val SERIAL_NUMBER_KEY = "SERIAL_NUMBER"

