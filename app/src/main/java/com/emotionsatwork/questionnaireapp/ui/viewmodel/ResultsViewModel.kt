package com.emotionsatwork.questionnaireapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionsatwork.questionnaireapp.data.QuestionnaireDao
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultsViewModel(
    private val dao: QuestionnaireDao,
) : ViewModel() {

    private val _userResult = MutableStateFlow<Map<PersonalityType, Int>>(mapOf())
    val userResult: StateFlow<Map<PersonalityType, Int>> = _userResult

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAll().collect { answers ->
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
                    foo[personalityType] = answers.filter {
                        it.personalityType == personalityType
                    }.sumOf {
                        it.answer
                    }
                }
                _userResult.emit(foo)
            }
        }
    }

    fun deleteAllAnswers() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.nukeAnswersDb()
        }
    }
}