package com.example.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun getNote(id: Int) : Note

    fun getAllNotes() : Flow<List<Note>>

    suspend fun deleteNote(id: Int)

    suspend fun editNote(note: Note)

    suspend fun addNote(
        title: String,
        description: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    )

    suspend fun switchPinnedStatus(id: Int)

    fun searchNotes(query: String): Flow<List<Note>>

}