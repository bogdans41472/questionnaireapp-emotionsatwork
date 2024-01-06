package com.emotionsatwork.questionnaireapp.ui.composables

import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.emotionsatwork.questionnaireapp.R

import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import java.util.concurrent.TimeUnit

@Composable
fun Results(
    viewModel: QuestionnaireViewModel,
) {
    val futureResult = viewModel.getResultForUser()
    val results = futureResult.get(5, TimeUnit.SECONDS)
    var selectedItem by remember {
        mutableStateOf(PersonalityType.UNKNOWN)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 20.dp,
                    top = 20.dp
                ),
            horizontalArrangement = Arrangement.Center
        ) {
            ClickablePieChart(
                modifier = Modifier.size(300.dp),
                results
            ) {
                selectedItem = it
            }
        }
        Divider(
            thickness = 1.dp, modifier = Modifier
                .padding(bottom = 4.dp)
                .background(Color.Black)
        )
        var tabIndex by remember {
            mutableIntStateOf(0)
        }
        val tabs = listOf(stringResource(id = R.string.summary_title), stringResource(id = R.string.exercises))

        Column(modifier = Modifier.fillMaxWidth()) {
            var shouldShowSummary by remember {
                mutableStateOf(true)
            }
            var shouldShowExercise by remember {
                mutableStateOf(false)
            }
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
                when(tabIndex) {
                    0 -> {
                        shouldShowSummary = true
                        shouldShowExercise = false
                    }
                    1 -> {
                        shouldShowExercise = true
                        shouldShowSummary = false
                    }

                }
            }
            if (shouldShowSummary) {
                showSummary(selectedItem = selectedItem)
            }
            if (shouldShowExercise) {
                showExercises(selectedItem = selectedItem)
            }
        }
    }
}

@Composable
fun showExercises(selectedItem: PersonalityType) {
    val exercises: String = when(selectedItem) {
        PersonalityType.UNBREAKABLE -> stringResource(id = R.string.unbreakable_exercises)
        PersonalityType.INSPECTOR -> stringResource(id = R.string.inspector_exercises)
        PersonalityType.SAVIOR -> stringResource(id = R.string.savior_exercises)
        PersonalityType.REJECTED -> stringResource(id = R.string.rejected_exercises)
        PersonalityType.PESSIMIST -> stringResource(id = R.string.pessimist_exercises)
        PersonalityType.DOER -> stringResource(id = R.string.doer_exercises)
        PersonalityType.CONFORMER -> stringResource(id = R.string.conformer_exercises)
        PersonalityType.DREAMER -> stringResource(id = R.string.dreamer_exercises)
        else -> ""
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 8.dp),
        text = exercises,
    )
}

@Composable
fun showSummary(selectedItem: PersonalityType) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
    ) {
        val personalitySummary: String = when (selectedItem) {
            PersonalityType.UNBREAKABLE -> stringResource(id = R.string.unbreakable_summary)
            PersonalityType.INSPECTOR -> stringResource(id = R.string.inspector_summary)
            PersonalityType.SAVIOR -> stringResource(id = R.string.savior_summary)
            PersonalityType.REJECTED -> stringResource(id = R.string.rejected_summary)
            PersonalityType.PESSIMIST -> stringResource(id = R.string.pessimist_summary)
            PersonalityType.DOER -> stringResource(id = R.string.doer_summary)
            PersonalityType.CONFORMER -> stringResource(id = R.string.conformer_summary)
            PersonalityType.DREAMER -> stringResource(id = R.string.dreamer_summary)
            else -> ""
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            text = personalitySummary,
            textAlign = TextAlign.Justify
        )
    }

}

@Composable
fun ClickablePieChart(
    modifier: Modifier = Modifier,
    results: List<Map<PersonalityType, Double>>,
    textColor: Color = Color.Black,
    clickedItem: (PersonalityType) -> Unit
) {
    val values = results.flatMap { it.values }
    val chartDegrees = 360f // circle shape
    val proportions = values.map {
        it * 100 / values.sum()
    }
    var startAngle = 270f

    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }
    var clickedItemIndex by remember {
        mutableIntStateOf(0)
    }
    val progressSize = mutableListOf<Float>()

    val density = LocalDensity.current
    val textFontSize = with(density) { 18.dp.toPx() }
    val textPaint = remember {
        Paint().apply {
            color = textColor.toArgb()
            textSize = textFontSize
            textAlign = Paint.Align.CENTER
        }
    }
    val animatable = remember {
        Animatable(-90f)
    }

    LaunchedEffect(angleProgress) {
        progressSize.add(angleProgress.first().toFloat())
        for (x in 1 until angleProgress.size) {
            progressSize.add(angleProgress[x].toFloat() + progressSize[x - 1])
        }
        animatable.animateTo(
            targetValue = 270f,
            animationSpec = tween(
                delayMillis = 4000,
                durationMillis = 2000
            )
        )
    }
    val selectedPersonalityType = results[clickedItemIndex].keys.elementAt(0)

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val canvasSize = constraints.maxWidth.coerceAtMost(constraints.maxWidth)
        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
        val canvasSizeDp = with(LocalDensity.current) { canvasSize.toDp() }
        Canvas(modifier = Modifier
            .size(canvasSizeDp)
            .pointerInput(values) {
                detectTapGestures { offset ->
                    val clickedAngle = touchPointToAngle(
                        width = canvasSize.toFloat(),
                        height = canvasSize.toFloat(),
                        touchX = offset.x,
                        touchY = offset.y
                    )
                    Log.i("Bogdan", "clickedAngle: $clickedAngle")
                    progressSize.forEachIndexed { index, item ->
                        if (clickedAngle <= item) {
                            clickedItemIndex = index
                            return@detectTapGestures
                        }
                    }
                }
            }
        ) {
            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = getColorToUse(results[index].keys.elementAt(0)),
                    startAngle = startAngle,
                    sweepAngle = angle.toFloat(),
                    useCenter = true,
                    size = size,

                    alpha = 0.65f
                )
                startAngle += angle.toFloat()
            }
            if (clickedItemIndex != -1) {
                drawIntoCanvas { canvas ->
                    Log.i("Bogdan", "Canvas drawIntoCanvas")
                    clickedItem.invoke(selectedPersonalityType)
                    val xPosition = (canvasSize / 2) + textFontSize / 4
                    val yPosition = (canvasSize / 2) + textFontSize / 4
//                    canvas.nativeCanvas.drawText(
//                        selectedPersonalityType.name,
//                        xPosition,
//                        yPosition,
//                        textPaint
//                    )
                    //first slice
                    val firstPersonalityString = (results[0].keys.elementAt(0)).toString()
                    val firstPersonalityPercentage = ((results[0].values.elementAt(0)*100).toString() + "%")
                    canvas.nativeCanvas.drawText(
                        firstPersonalityString,
                        canvasSize - (canvasSize / 16).toFloat(),
                        350f,
                        textPaint
                    )
                    canvas.nativeCanvas.drawText(
                        firstPersonalityPercentage,
                        canvasSize - (canvasSize / 16).toFloat(),
                        450f,
                        textPaint
                    )
                    //second slice
                    val secondPersonalityString = (results[1].keys.elementAt(0)).toString()
                    val secondPersonalityPercentage = ((results[1].values.elementAt(0)*100).toString() + "%")

                    canvas.nativeCanvas.drawText(
                        secondPersonalityString,
                        (canvasSize / 2).toFloat(),
                        950f,
                        textPaint
                    )
                    canvas.nativeCanvas.drawText(
                        secondPersonalityPercentage,
                        (canvasSize / 2).toFloat(),
                        1050f,
                        textPaint
                    )

                    // third slice
                    val thirdPersonalityString = (results[2].keys.elementAt(0)).toString()
                    val thirdPersonalityPercentage = ((results[2].values.elementAt(0)*100).toString() + "%")

                    canvas.nativeCanvas.drawText(
                        thirdPersonalityString,
                        (canvasSize / 8).toFloat(),
                        350f,
                        textPaint
                    )
                    canvas.nativeCanvas.drawText(
                        thirdPersonalityPercentage,
                        (canvasSize / 8).toFloat(),
                        450f,
                        textPaint
                    )
//                    canvas.nativeCanvas.drawText(
//                        (results[clickedItemIndex].values.elementAt(0)*100).toString() + "%",
//                        xPosition,
//                        yPosition + 80f,
//                        textPaint
//                    )
                }
            }
        }
    }
}

private fun touchPointToAngle(
    width: Float,
    height: Float,
    touchX: Float,
    touchY: Float
): Double {
    val chartDegrees = 360f
    val x = touchX - (width * 0.5f)
    val y = touchY - (height * 0.5f)
    var angle = Math.toDegrees(kotlin.math.atan2(y.toDouble(), x.toDouble()) + Math.PI / 2)
    angle = if (angle < 0) angle + chartDegrees else angle
    return angle
}

fun getColorToUse(personalityType: PersonalityType): Color {
    return when (personalityType) {
        PersonalityType.DREAMER -> Color.Cyan
        PersonalityType.DOER -> Color.Green
        PersonalityType.PESSIMIST -> Color.Black
        PersonalityType.CONFORMER -> Color.LightGray
        PersonalityType.REJECTED -> Color.Red
        PersonalityType.SAVIOR -> Color.Magenta
        PersonalityType.INSPECTOR -> Color.Blue
        PersonalityType.UNBREAKABLE -> Color.Yellow
        else -> Color.White
    }
}

fun getSelectedColor(color: Color): Color {
    return Color.Black
}

@Composable
fun MyCircularProgress(loadState: Boolean) {
    if (loadState) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp, 80.dp)
            )
        }
    }
}