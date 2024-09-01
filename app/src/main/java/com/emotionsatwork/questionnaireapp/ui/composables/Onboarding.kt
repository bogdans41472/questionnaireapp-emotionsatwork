package com.emotionsatwork.questionnaireapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionsatwork.questionnaireapp.R
import com.emotionsatwork.questionnaireapp.datamodel.Edition

@Composable
fun Onboarding(
    chosenEdition: (Edition) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color(0xFFF3F3F3))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val painter = painterResource(id = R.drawable.emotions_at_work_cover)
            Image(
                painter = painter,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(300.dp, 450.dp)
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                alignment = Alignment.TopCenter
            )
            Column {
                Text(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.onboarding),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    style = TextStyle(fontSize = 24.sp)
                )
            }
            Button(
                onClick = {
                    chosenEdition.invoke(Edition.BOOK)
                },
                elevation = ButtonDefaults.buttonElevation(5.dp),
            ) {
                val textColor = Color.Unspecified.takeOrElse {
                    LocalTextStyle.current.color.takeOrElse {
                        LocalContentColor.current
                    }
                }

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

            Button(
                onClick = {
                    chosenEdition.invoke(Edition.SEMINAR)
                },
                elevation = ButtonDefaults.buttonElevation(5.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                val textColor = Color.Unspecified.takeOrElse {
                    LocalTextStyle.current.color.takeOrElse {
                        LocalContentColor.current
                    }
                }
                
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
        }
    }
}

@Preview
@Composable
fun OnboardingPreview() {
    Onboarding {}
}



