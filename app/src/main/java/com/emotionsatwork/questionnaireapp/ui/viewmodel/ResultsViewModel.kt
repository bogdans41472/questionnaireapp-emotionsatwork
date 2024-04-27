package com.emotionsatwork.questionnaireapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionsatwork.questionnaireapp.data.QuestionDb
import com.emotionsatwork.questionnaireapp.data.QuestionnaireDao
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class ResultsViewModel(
    private val dao: QuestionnaireDao,
): ViewModel() {

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
                getScoreForPersonalityType(answers, it)
            }.sortedByDescending {
                it.values.maxByOrNull { score -> score }
            }
            personalityResultType.complete(result)
        }
        return personalityResultType
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

}