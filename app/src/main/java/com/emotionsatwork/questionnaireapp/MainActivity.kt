package com.emotionsatwork.questionnaireapp

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
import com.emotionsatwork.questionnaireapp.ui.composables.Onboarding
import com.emotionsatwork.questionnaireapp.ui.composables.Questionnaire
import com.emotionsatwork.questionnaireapp.ui.composables.Results
import com.emotionsatwork.questionnaireapp.ui.viewmodel.QuestionnaireViewModel
import com.emotionsatwork.questionnaireapp.ui.theme.QuestionnaireAppTheme
import com.emotionsatwork.questionnaireapp.ui.viewmodel.ResultsViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuestionnaireAppTheme {
                val navController = rememberNavController()
                var chosenEdition = remember { Edition.SEMINAR }
                NavHost(navController = navController, startDestination = "onboarding") {
                    // replace login with onboarding screen
                    navigation(
                        startDestination = "login",
                        route = "onboarding"
                    ) {
                        composable("login") {
                            Onboarding {
                                chosenEdition = it
                                navController.navigate("questionnaire")
                            }
                        }
                    }
                    navigation(
                        startDestination = "questionnaire_overview",
                        route = "questionnaire"
                    ) {
                        composable("questionnaire_overview") {
                            val viewModel = QuestionnaireViewModel(
                                QuestionnaireLoader(assetManager = assets).loadQuestions(chosenEdition),
                                getDb().questionnaireDao()
                            ) {
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
                                navController.navigate("onboarding")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDb() = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()
}