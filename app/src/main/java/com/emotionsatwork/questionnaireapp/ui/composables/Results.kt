package com.emotionsatwork.questionnaireapp.ui.composables

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.emotionsatwork.questionnaireapp.R
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Results(
    viewModel: QuestionnaireViewModel,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
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
        IconButton(
            onClick = {
                openBottomSheet = true
            }, modifier = Modifier
                .align(Alignment.End)
        ) {
            Icon(Icons.Filled.Info, contentDescription = "More info")
        }
        Divider(
            thickness = 1.dp, modifier = Modifier
                .padding(bottom = 4.dp)
                .background(Color.Black)
        )
        var tabIndex by remember {
            mutableIntStateOf(0)
        }
        val tabs = listOf(
            stringResource(id = R.string.summary_title),
            stringResource(id = R.string.exercises)
        )

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
                when (tabIndex) {
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
                ShowSummary(selectedItem = selectedItem)
            }
            if (shouldShowExercise) {
                ShowExercises(selectedItem = selectedItem)
            }
        }
    }

    var edgeToEdgeEnabled by remember { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    if (openBottomSheet) {
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp),
                    text = stringResource(R.string.info_tutorial),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
fun ShowExercises(selectedItem: PersonalityType) {
    val exercises: String = when (selectedItem) {
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 4.dp, end = 4.dp, top = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = exercises,
        )
        val url = stringResource(id = R.string.url_to_website)
        val context = LocalContext.current
        TextButton(
            onClick = {
                val website = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, website)
                startActivity(context, intent, null)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Bottom),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.url_details) + stringResource(id = R.string.url_to_website)
            )
        }
    }
}

@Composable
fun ShowSummary(selectedItem: PersonalityType) {
    Column(
        modifier = Modifier
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
    val selectedTextPaint = remember {
        Paint().apply {
            color = Color.White.toArgb()
            textSize = textFontSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
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
                    clickedItem.invoke(selectedPersonalityType)
                    //first slice
                    val firstPersonalityString = (results[0].keys.elementAt(0)).toString()
                    val firstPersonalityPercentage =
                        ((results[0].values.elementAt(0) * 100).toString() + "%")
                    canvas.nativeCanvas.drawText(
                        firstPersonalityString,
                        800f,
                        400f,
                        if (clickedItemIndex == 0) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
                    canvas.nativeCanvas.drawText(
                        firstPersonalityPercentage,
                        800f,
                        480f,
                        if (clickedItemIndex == 0) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
                    //second slice
                    val secondPersonalityString = (results[1].keys.elementAt(0)).toString()
                    val secondPersonalityPercentage =
                        ((results[1].values.elementAt(0) * 100).toString() + "%")

                    canvas.nativeCanvas.drawText(
                        secondPersonalityString,
                        (canvasSize / 2).toFloat(),
                        800f,
                        if (clickedItemIndex == 1) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
                    canvas.nativeCanvas.drawText(
                        secondPersonalityPercentage,
                        (canvasSize / 2).toFloat(),
                        900f,
                        if (clickedItemIndex == 1) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
                    // third slice
                    val thirdPersonalityString = (results[2].keys.elementAt(0)).toString()
                    val thirdPersonalityPercentage =
                        ((results[2].values.elementAt(0) * 100).toString() + "%")

                    canvas.nativeCanvas.drawText(
                        thirdPersonalityString,
                        300f,
                        400f,
                        if (clickedItemIndex == 2) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
                    canvas.nativeCanvas.drawText(
                        thirdPersonalityPercentage,
                        300f,
                        480f,
                        if (clickedItemIndex == 2) {
                            selectedTextPaint
                        } else {
                            textPaint
                        }
                    )
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
        PersonalityType.DREAMER -> Color(0xffd277cf)
        PersonalityType.DOER -> Color(0xfff9893c)
        PersonalityType.PESSIMIST -> Color(0xffff5757)
        PersonalityType.CONFORMER -> Color(0xff6c8bcb)
        PersonalityType.REJECTED -> Color(0xffe1e2ec)
        PersonalityType.SAVIOR -> Color(0xffffbf00)
        PersonalityType.INSPECTOR -> Color(0xff45a297)
        PersonalityType.UNBREAKABLE -> Color(0xff4131c8)
        else -> Color.White
    }
}