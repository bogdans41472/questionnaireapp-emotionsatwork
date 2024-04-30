package com.emotionsatwork.questionnaireapp.ui.viewmodel


import android.os.Handler
import android.os.Looper
import android.util.Log
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
    private val onQuestionnaireComplete: () -> Unit
) : ViewModel() {

    private val answers = mutableMapOf<Int, Int>()
    private var currentQuestionPosition: Int = 0
    private var _question: MutableStateFlow<Question> =
        MutableStateFlow(questions[currentQuestionPosition])

    val questionFlow = _question.asStateFlow()

    fun submitAnswerForQuestion(answer: Float) {
        answers[_question.value.id] = answer.toInt()
        if (currentQuestionPosition < questions.size - 1) {
            currentQuestionPosition += 1
            _question.value = questions[currentQuestionPosition]
        } else {

            storeQuestionAnswers {
                Handler(Looper.getMainLooper()).post {
                    onQuestionnaireComplete.invoke()
                }
            }
        }
    }

    private fun storeQuestionAnswers(isDoneStoringStuff: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            answers.forEach {
                dao.insertQuestion(
                    QuestionDb(
                        it.key,
                        questions[it.key].title,
                        it.value,
                        questions[it.key].personalityType
                    )
                )
            }
            isDoneStoringStuff.invoke()
        }
    }
}
