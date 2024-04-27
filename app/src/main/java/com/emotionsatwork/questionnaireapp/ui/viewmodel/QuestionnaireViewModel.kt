package com.emotionsatwork.questionnaireapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionsatwork.questionnaireapp.data.QuestionDb
import com.emotionsatwork.questionnaireapp.data.QuestionnaireDao
import com.emotionsatwork.questionnaireapp.datamodel.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class QuestionnaireViewModel(
    val questions: List<Question>,
    private val dao: QuestionnaireDao,
    val onQuestionnaireComplete: () -> Unit
) : ViewModel() {

    private val answers = mutableMapOf<Int, Question>()
    private var currentQuestionPosition: Int = 0
    private var _question: MutableStateFlow<Question> =
        MutableStateFlow(questions[currentQuestionPosition])

    val questionFlow = _question.asStateFlow()

    fun submitAnswerForQuestion(answer: Float) {
        answers[answer.toInt()] = _question.value
        if (currentQuestionPosition < questions.size) {
            storeQuestionAnswer(answer)
            val questionToUpdate = questions[currentQuestionPosition]
            currentQuestionPosition += 1
            _question.value = questionToUpdate
        } else {
            onQuestionnaireComplete.invoke()
        }
    }

    private fun storeQuestionAnswer(answer: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentQuestion = _question.value
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
}
