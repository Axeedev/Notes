package com.example.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {

    @Query("SELECT * FROM NOTES ORDER BY updatedAt DESC")
    fun getNotes() : Flow<List<NoteDbModel>>

    @Query("SELECT * FROM NOTES WHERE id == :id")
    suspend fun getNodeById(id: Int): NoteDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteDbModel: NoteDbModel)

    @Query("DELETE FROM NOTES WHERE id == :id")
    suspend fun deleteNote(id: Int)

    @Update
    suspend fun edit(noteDbModel: NoteDbModel)

    @Query("SELECT * FROM NOTES WHERE title LIKE '%' || :query || '%' OR content = '%'||:query||'%'")
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Query("UPDATE NOTES SET isPinned = NOT isPinned where id == :id")
    suspend fun switchPinnedStatus(id: Int)
}