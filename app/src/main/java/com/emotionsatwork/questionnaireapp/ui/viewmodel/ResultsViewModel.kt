package com.emotionsatwork.questionnaireapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionsatwork.questionnaireapp.data.QuestionnaireDao
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ResultsViewModel(
    private val dao: QuestionnaireDao,
) : ViewModel() {

    fun getUserResult(): Map<PersonalityType, Int> {
        return runBlocking {
            val bla = dao.getAll().first()
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
            val foo = mutableMapOf<PersonalityType, Int>()
            personalityTypes.forEach { personalityType ->
                foo[personalityType] = bla.filter {
                    it.personalityType == personalityType
                }.sumOf {
                    it.answer
                }
            }
            return@runBlocking foo.toList().sortedByDescending { it.second }.toMap()
        }
    }

    fun deleteAllAnswers() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.nukeAnswersDb()
        }
    }
}