package com.emotionsatwork.questionnaireapp.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.emotionsatwork.questionnaireapp.datamodel.PersonalityType
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionnaireDao {

    @Query("SELECT * FROM QuestionDb")
    fun getAll(): Flow<List<QuestionDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestion(question: QuestionDb)

    @Delete
    fun deleteQuestion(question: QuestionDb)

    @Query("DELETE FROM QuestionDb")
    fun nukeAnswersDb()

    @Update
    fun updateQuestion(question: QuestionDb)
}

@Entity
data class QuestionDb(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "answer") val answer: Int,
    @ColumnInfo(name = "personalityType") val personalityType: PersonalityType
)