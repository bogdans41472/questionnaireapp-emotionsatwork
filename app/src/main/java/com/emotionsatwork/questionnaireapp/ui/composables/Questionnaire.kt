package com.emotionsatwork.questionnaireapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emotionsatwork.questionnaireapp.ui.theme.PrimaryText
import com.emotionsatwork.questionnaireapp.ui.theme.QuestionnaireAppTheme
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import kotlin.math.roundToInt

@Composable
fun Questionnaire(
    viewModel: QuestionnaireViewModel,
    onQuestionnaireComplete: (Boolean) -> Unit
) {
    QuestionnaireAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
        ) {
            Question(viewModel = viewModel, onQuestionnaireComplete)
        }
    }
}


@Composable
fun Question(viewModel: QuestionnaireViewModel, onQuestionnaireComplete: (Boolean) -> Unit) {
    val question by viewModel.questionFlow.collectAsStateWithLifecycle()
    if (question == null) {
        onQuestionnaireComplete.invoke(true)
    } else {
        QuestionnaireAppTheme {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F3F3))
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .align(CenterVertically),
                    shape = RoundedCornerShape(30.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    colors = cardColors(
                        containerColor = Color(0xFFFFFFFF),
                        contentColor = Color.Black
                    )
                ) {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                color = PrimaryText,
                                text = question!!.title,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column(
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            var sliderPosition by remember(0.0f) { mutableFloatStateOf(0.0f) }
                            val minSliderValue = 0f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {

                                    // slider
                                    val maxSliderValue = 10f
                                    Slider(
                                        value = sliderPosition,
                                        onValueChange = { sliderPosition = it },
                                        steps = 10,
                                        valueRange = minSliderValue..maxSliderValue,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Never",
                                    modifier = Modifier.align(CenterStart),
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Always",
                                    modifier = Modifier.align(CenterEnd),
                                    textAlign = TextAlign.End,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "Selected: ${sliderPosition.roundToInt()}",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Button(
                                onClick = {
                                    // Get value from radio group
                                    viewModel.submitAnswerForQuestion(sliderPosition)
                                    sliderPosition = minSliderValue
                                },
                                elevation = ButtonDefaults.buttonElevation(5.dp)
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
                                BasicText(
                                    text = "Submit answer",
                                    modifier = Modifier.padding(16.dp),
                                    style = mergedStyle
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .padding(top = 12.dp, end = 5.dp)
                                    .align(Alignment.End),
                                textAlign = TextAlign.End,
                                text = "Question ${question!!.id + 1} out of 40"
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SliderLabel(label: String, minWidth: Dp, modifier: Modifier = Modifier) {
        Text(
            label,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = modifier
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .defaultMinSize(minWidth = minWidth)
        )
    }
}