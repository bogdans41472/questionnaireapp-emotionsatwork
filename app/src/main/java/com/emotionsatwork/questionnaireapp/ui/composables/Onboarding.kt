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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionsatwork.questionnaireapp.R
import com.emotionsatwork.questionnaireapp.datamodel.Edition
import com.emotionsatwork.questionnaireapp.extensions.ONBOARDING_COMPLETED

@Composable
fun Onboarding(
    sharedPreferences: SharedPreferences,
    chosenEdition: (Edition) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .background(Color(0xFFF3F3F3)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val painter = painterResource(id = R.drawable.ic_launcher)
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
            Row(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.onboarding),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        style = TextStyle(fontSize = 24.sp)
                    )
                }
            }
            Button(
                onClick = {
                    onboardingCompleted(sharedPreferences)
                    chosenEdition.invoke(Edition.SEMINAR)
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
                Text(
                    text = "Seminar Edition",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = mergedStyle,
                    fontSize = 16.sp,
                    onTextLayout = {},
                )
            }
            Button(
                onClick = {
                    onboardingCompleted(sharedPreferences)
                    chosenEdition.invoke(Edition.BOOK)
                },
                elevation = ButtonDefaults.buttonElevation(5.dp),
                modifier = Modifier.padding(top = 16.dp)
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
                Text(
                    text = "Book Edition",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = mergedStyle,
                    fontSize = 16.sp,
                    onTextLayout = {},
                )
            }
        }
    }
}

fun onboardingCompleted(sharedPreferences: SharedPreferences) {
    sharedPreferences.edit()
        .putBoolean(ONBOARDING_COMPLETED, true)
        .apply()
}



