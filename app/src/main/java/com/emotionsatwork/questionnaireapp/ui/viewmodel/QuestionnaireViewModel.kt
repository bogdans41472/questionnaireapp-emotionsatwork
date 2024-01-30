package com.emotionsatwork.questionnaireapp.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionsatwork.questionnaireapp.data.QuestionDb
import com.emotionsatwork.questionnaireapp.data.QuestionnaireDao
import com.emotionsatwork.questionnaireapp.data.QuestionnaireLoader
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import com.emotionsatwork.questionnaireapp.datamodel.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.math.roundToInt


class QuestionnaireViewModel(
    questionLoader: QuestionnaireLoader,
    private val sharedPreferences: SharedPreferences,
    private val dao: QuestionnaireDao
) : ViewModel() {

    private val questions: List<Question> = questionLoader.loadQuestions()
    private var currentQuestionPosition: Int = 0

    private var _question: MutableStateFlow<Question?> = if (currentQuestionPosition != 39
        && !getLastAnsweredQuestionIndex()) {
        MutableStateFlow(questions[currentQuestionPosition])
    } else {
        MutableStateFlow(null)
    }
    val questionFlow = _question.asStateFlow()

    private val answers = mutableMapOf<Int, Question>()

    fun submitAnswerForQuestion(answer: Float) {
        answers[answer.toInt()] = _question.value!!
        if (currentQuestionPosition != 39) {
            storeLastQuestionAnswered(answer)
            val questionToUpdate = questions[currentQuestionPosition+1]
            currentQuestionPosition += 1
            _question.value = questionToUpdate
            return
        } else {
            storeLastQuestionIndex(true)
        }
    }

    fun getResultForUser(): CompletableFuture<List<Map<PersonalityType, Double>>> {
        val personalityResultType: CompletableFuture<List<Map<PersonalityType, Double>>> =
            CompletableFuture()
        viewModelScope.launch(Dispatchers.IO) {
            val answers = dao.getAll()

            val personalityTypes = setOf(
                PersonalityType.Doer,
                PersonalityType.Unbreakable,
                PersonalityType.Rejected,
                PersonalityType.Savior,
                PersonalityType.Inspector,
                PersonalityType.Pessimist,
                PersonalityType.Conformer,
                PersonalityType.Dreamer
            )
            val result = personalityTypes.map {
                val scoreForPersonality = getScoreForPersonalityType(answers, it)
                scoreForPersonality
            }.sortedByDescending {
                it.values.maxByOrNull { score -> score }!!
            }.subList(0, 3)
                .calculatePercentage()
            personalityResultType.complete(result)
        }
        return personalityResultType
    }

    private fun List<Map<PersonalityType, Double>>.calculatePercentage(): List<Map<PersonalityType, Double>> {
        val totalScore = getTotalScore(this)
        val processedResultsInPercentages = mutableListOf<Map<PersonalityType, Double>>()
        this.forEach { entry ->
            entry.mapValues { value ->
                val percentage = value.value / totalScore
                val trimmedPercentage = (percentage * 100.0).roundToInt() / 100.0
                processedResultsInPercentages.add(
                    mapOf(Pair(entry.keys.first(), trimmedPercentage))
                )
            }
        }
        return processedResultsInPercentages
    }

    private fun getTotalScore(result: List<Map<PersonalityType, Double>>): Double {
        var totalValue = 0.0
        result.forEach {
            totalValue += it.values.first()
        }
        return totalValue
    }

    private fun getScoreForPersonalityType(
        answers: List<QuestionDb>,
        personalityType: PersonalityType
    ): MutableMap<PersonalityType, Double> =
        mutableMapOf(Pair(personalityType, answers.filter {
            it.personalityType == personalityType
        }.sumOf {
            it.answer.toDouble()
        }))

    private fun getLastAnsweredQuestionIndex(): Boolean {
        return sharedPreferences.getBoolean(LAST_QUESTION_INDEX, false)
    }

    private fun storeLastQuestionIndex(isComplete: Boolean) {
        sharedPreferences.edit().putBoolean(LAST_QUESTION_INDEX, isComplete)
            .apply()
    }

    private fun storeLastQuestionAnswered(answer: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentQuestion = _question.value!!
            dao.insertQuestion(
                QuestionDb(
                    currentQuestion.id,
                    currentQuestion.title,
                    answer.toInt(),
                    currentQuestion.personalityType
                )
            )
        }
    }

    companion object {
        private const val LAST_QUESTION_INDEX = "LAST_INDEX"
    }
}
