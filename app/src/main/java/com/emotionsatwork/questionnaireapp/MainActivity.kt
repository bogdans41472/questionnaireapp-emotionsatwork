package com.emotionsatwork.questionnaireapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.emotionsatwork.questionnaireapp.data.AppDatabase
import com.emotionsatwork.questionnaireapp.data.QuestionnaireLoader
import com.emotionsatwork.questionnaireapp.datamodel.Edition
import com.emotionsatwork.questionnaireapp.extensions.MAIN_SHARED_PREFS
import com.emotionsatwork.questionnaireapp.extensions.QUESTIONNAIRE_COMPLETE_KEY
import com.emotionsatwork.questionnaireapp.ui.composables.ISBNCheck
import com.emotionsatwork.questionnaireapp.ui.composables.Onboarding
import com.emotionsatwork.questionnaireapp.ui.composables.Questionnaire
import com.emotionsatwork.questionnaireapp.ui.composables.Results
import com.emotionsatwork.questionnaireapp.ui.theme.QuestionnaireAppTheme
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import com.emotionsatwork.questionnaireapp.ui.viewmodel.ResultsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuestionnaireAppTheme {
                val navController = rememberNavController()
                var chosenEdition = remember { Edition.SEMINAR }
                NavHost(navController = navController, startDestination = getStartComposable()) {
                    navigation(
                        startDestination = "editionChooser",
                        route = "onboarding"
                    ) {
                        composable("editionChooser") {
                            Onboarding {
                                chosenEdition = it
                                if (chosenEdition == Edition.BOOK) {
                                    navController.navigate("isbnCheck")
                                } else {
                                    navController.navigate("questionnaire")
                                }
                            }
                        }
                    }
                    navigation(
                        startDestination = "questionnaire_overview",
                        route = "questionnaire"
                    ) {
                        composable("isbnCheck") {
                            ISBNCheck {
                                navController.navigate("questionnaire")
                            }
                        }
                        composable("questionnaire_overview") {
                            val viewModel = QuestionnaireViewModel(
                                QuestionnaireLoader(assetManager = assets).loadQuestions(
                                    chosenEdition
                                ),
                                getDb().questionnaireDao()
                            ) {
                                updateQuestionnaireCompleteness(true)
                                navController.navigate("results")
                            }
                            Questionnaire(viewModel)
                        }
                    }

                    // show congratulations
                    navigation(
                        startDestination = "results_overview",
                        route = "results"
                    ) {
                        composable("results_overview") {
                            val viewModel = ResultsViewModel(getDb().questionnaireDao())
                            Results(viewModel) {
                                updateQuestionnaireCompleteness(false)
                                navController.navigate("onboarding")
                            }
                        }
                    }
                }
            }
        }
    }


    private fun updateQuestionnaireCompleteness(isComplete: Boolean) {
        applicationContext.getSharedPreferences(MAIN_SHARED_PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(QUESTIONNAIRE_COMPLETE_KEY, isComplete)
            .apply()
    }

    private fun getStartComposable(): String {
        val isQuestionnaireCompleted =
            applicationContext.getSharedPreferences(MAIN_SHARED_PREFS, Context.MODE_PRIVATE)
                .getBoolean(QUESTIONNAIRE_COMPLETE_KEY, false)
        return if (isQuestionnaireCompleted) {
            "results"
        } else {
            "onboarding"
        }
    }

    private fun getDb() = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()
}