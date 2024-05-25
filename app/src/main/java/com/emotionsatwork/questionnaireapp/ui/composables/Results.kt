package com.emotionsatwork.questionnaireapp.ui.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.emotionsatwork.questionnaireapp.R
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import com.emotionsatwork.questionnaireapp.ui.theme.PrimaryDark
import com.emotionsatwork.questionnaireapp.ui.viewmodel.ResultsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Results(
    viewModel: ResultsViewModel,
    retakeAssessment: () -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val results = viewModel.getUserResult()
    var selectedItem: PersonalityType? by remember {
        mutableStateOf(results.getHighestPersonalityType())
    }
    val scrollState = rememberScrollState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 10.dp)
                .verticalScroll(scrollState)
        ) {
            Column {
                results.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (selectedItem == it.key) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = it.key
                                    }
                                    .padding(4.dp)
                            ) {
                                ConstraintLayout {
                                    val (score, personalityType) = createRefs()
                                    Text(
                                        text = "${it.value}",
                                        modifier = Modifier
                                            .border(
                                                width = 2.dp,
                                                color = PrimaryDark,
                                                shape = RectangleShape
                                            )
                                            .padding(4.dp)
                                            .constrainAs(score) {
                                                absoluteLeft
                                            }
                                    )
                                    Text(
                                        text = "${it.key}",
                                        Modifier
                                            .constrainAs(personalityType) {
                                                start.linkTo(score.end, margin = 16.dp)
                                                top.linkTo(parent.top)
                                                bottom.linkTo(parent.bottom)
                                            },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = it.key
                                    }
                                    .padding(4.dp),
                            ) {
                                ConstraintLayout {
                                    val (score, personalityType) = createRefs()
                                    Text(
                                        text = "${it.value}",
                                        modifier = Modifier
                                            .border(
                                                width = 2.dp,
                                                color = PrimaryDark,
                                                shape = RectangleShape
                                            )
                                            .padding(4.dp)
                                            .constrainAs(score) {
                                                absoluteLeft
                                            }
                                    )
                                    Text(
                                        text = "${it.key}",
                                        Modifier.constrainAs(personalityType) {
                                            start.linkTo(score.end, margin = 16.dp)
                                            top.linkTo(parent.top)
                                            bottom.linkTo(parent.bottom)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val showConfirmationDialog = remember { mutableStateOf(false) }
                if (showConfirmationDialog.value) {
                    ConfirmationDialog(
                        onDismissRequest = { showConfirmationDialog.value = false },
                        onConfirmation = {
                            viewModel.deleteAllAnswers()
                            retakeAssessment.invoke()
                        }
                    )
                }
                SmallFloatingActionButton(
                    onClick = {
                        showConfirmationDialog.value = true
                    },
                    Modifier
                        .padding(bottom = 10.dp)
                ) {
                    Icon(Icons.Filled.Refresh, "Retake assessment")
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(4f)
                )
                IconButton(
                    onClick = {
                        openBottomSheet = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "More info")
                }
            }

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
                TabRow(
                    selectedTabIndex = tabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White
                )
                {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(text = title) },
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
            val edgeToEdgeEnabled by remember { mutableStateOf(false) }
            val skipPartiallyExpanded by remember { mutableStateOf(false) }
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
    }
}

private fun Map<PersonalityType, Int>.getHighestPersonalityType(): PersonalityType? {
    var mapOfPersonalityTypeAndScore: MutableMap<PersonalityType, Int> = mutableMapOf()
    var maxValue = 0
    if (this.isNotEmpty()) {
        this.forEach {
            if (it.value > maxValue) {
                maxValue = it.value
                mapOfPersonalityTypeAndScore = mutableMapOf(Pair(it.key, it.value))
            }
        }
        return mapOfPersonalityTypeAndScore.keys.first()
    }
    return null
}

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = "Retake assessment")
        },
        text = {
            Text(text = "Would you like to retake the assessment?")
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
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )

}

@Composable
fun ShowExercises(selectedItem: PersonalityType?) {
    val exercises: String = when (selectedItem) {
        PersonalityType.Unbreakable -> stringResource(id = R.string.unbreakable_exercises)
        PersonalityType.Inspector -> stringResource(id = R.string.inspector_exercises)
        PersonalityType.Savior -> stringResource(id = R.string.savior_exercises)
        PersonalityType.Rejected -> stringResource(id = R.string.rejected_exercises)
        PersonalityType.Pessimist -> stringResource(id = R.string.pessimist_exercises)
        PersonalityType.Doer -> stringResource(id = R.string.doer_exercises)
        PersonalityType.Conformer -> stringResource(id = R.string.conformer_exercises)
        PersonalityType.Dreamer -> stringResource(id = R.string.dreamer_exercises)
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
                    .padding(bottom = 30.dp)
                    .align(Alignment.Bottom),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.url_details) + stringResource(id = R.string.url_to_website)
            )
        }
    }
}

@Composable
fun ShowSummary(selectedItem: PersonalityType?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        val personalitySummary: String = when (selectedItem) {
            PersonalityType.Unbreakable -> stringResource(id = R.string.unbreakable_summary)
            PersonalityType.Inspector -> stringResource(id = R.string.inspector_summary)
            PersonalityType.Savior -> stringResource(id = R.string.savior_summary)
            PersonalityType.Rejected -> stringResource(id = R.string.rejected_summary)
            PersonalityType.Pessimist -> stringResource(id = R.string.pessimist_summary)
            PersonalityType.Doer -> stringResource(id = R.string.doer_summary)
            PersonalityType.Conformer -> stringResource(id = R.string.conformer_summary)
            PersonalityType.Dreamer -> stringResource(id = R.string.dreamer_summary)
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