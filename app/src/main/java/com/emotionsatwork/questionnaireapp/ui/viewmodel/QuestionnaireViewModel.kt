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
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt


class QuestionnaireViewModel(
    questionLoader: QuestionnaireLoader,
    private val sharedPreferences: SharedPreferences,
    private val dao: QuestionnaireDao
) : ViewModel() {

    private val questions: List<Question> = questionLoader.loadQuestions()
    private var currentQuestionPosition: Int = getLastAnsweredQuestionIndex()

    private var _question: MutableStateFlow<Question?> = if (currentQuestionPosition != 39) {
        MutableStateFlow(questions[currentQuestionPosition])
    } else {
        MutableStateFlow(null)
    }
    val questionFlow = _question.asStateFlow()

    private val answers = mutableMapOf<Int, Question>()

    fun submitAnswerForQuestion(answer: Float) {
        answers[answer.toInt()] = questionFlow.value!!
        if (currentQuestionPosition < questions.size) {
            val toUpdate = currentQuestionPosition++
            storeLastQuestionIndex(toUpdate)
            storeLastQuestionAnswered(answer)
            val questionToUpdate = questions[toUpdate]
            _question.value = questionToUpdate
            return
        }

        _question.value = null
    }

    fun getResultForUser(): CompletableFuture<List<Map<PersonalityType, Double>>> {
        val personalityResultType: CompletableFuture<List<Map<PersonalityType, Double>>> =
            CompletableFuture()
        viewModelScope.launch(Dispatchers.IO) {
            val answers = dao.getAll()

            val personalityTypes = setOf(
                PersonalityType.DOER,
                PersonalityType.UNBREAKABLE,
                PersonalityType.REJECTED,
                PersonalityType.SAVIOR,
                PersonalityType.INSPECTOR,
                PersonalityType.PESSIMIST,
                PersonalityType.CONFORMER,
                PersonalityType.DREAMER
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
                    mapOf(Pair(entry.keys.first(), trimmedPercentage)))
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


    private fun getLastAnsweredQuestionIndex(): Int {
        return sharedPreferences.getInt(LAST_QUESTION_INDEX, 0)
    }

    private fun storeLastQuestionIndex(toUpdate: Int) {
        sharedPreferences.edit().putInt(LAST_QUESTION_INDEX, toUpdate)
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
