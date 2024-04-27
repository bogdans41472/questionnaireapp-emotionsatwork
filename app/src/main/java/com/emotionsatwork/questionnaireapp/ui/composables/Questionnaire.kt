package com.emotionsatwork.questionnaireapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emotionsatwork.questionnaireapp.datamodel.Edition
import com.emotionsatwork.questionnaireapp.ui.theme.PrimaryText
import com.emotionsatwork.questionnaireapp.ui.theme.QuestionnaireAppTheme
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import kotlin.math.roundToInt

@Composable
fun Questionnaire(
    viewModel: QuestionnaireViewModel,
) {
    QuestionnaireAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally,
        ) {
            Question(viewModel = viewModel)
        }
    }
}

// add info icon with instructions on how to answer questions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Question(viewModel: QuestionnaireViewModel) {
    val question by viewModel.questionFlow.collectAsStateWithLifecycle()
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
                            text = question.title,
                            textAlign = TextAlign.Center
                        )
                    }
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        var sliderPosition by remember { mutableFloatStateOf(5f) }
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
                                val interactionSource = remember { MutableInteractionSource() }
                                Slider(
                                    value = sliderPosition,
                                    onValueChange = { sliderPosition = it },
                                    steps = 9,
                                    valueRange = minSliderValue..maxSliderValue,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .height(20.dp)
                                        .clearAndSetSemantics {
                                            contentDescription =
                                                "Slider position ${sliderPosition.roundToInt()}"
                                        },
                                    track = { sliderPositions ->
                                        SliderDefaults.Track(
                                            modifier = Modifier.scale(scaleX = 1f, scaleY = 2.15f),
                                            sliderState = sliderPositions
                                        )
                                    },
                                    thumb = {
                                        SliderDefaults.Thumb(
                                            modifier = Modifier.scale(scaleX = 1f, scaleY = 1f),
                                            interactionSource = interactionSource,
                                        )
                                    }
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
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                        Button(
                            onClick = {
                                // Get value from radio group
                                viewModel.submitAnswerForQuestion(sliderPosition)
                                sliderPosition = 5f
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
                            Text(
                                text = "Submit answer",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(16.dp),
                                style = mergedStyle
                            )
                        }

                        Text(
                            modifier = Modifier
                                .padding(top = 12.dp, end = 5.dp)
                                .align(Alignment.End),
                            textAlign = TextAlign.End,
                            text = "Question ${question.id + 1} out of ${viewModel.questions.size}"
                        )
                    }
                }
            }
        }
    }
}