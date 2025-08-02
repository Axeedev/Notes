package com.example.notes.data

import android.util.Log
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryIml @Inject constructor(private val notesDao: NotesDao) : NotesRepository {


    override suspend fun getNote(id: Int): Note {
        return notesDao.getNodeById(id).toNote()
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getNotes().map { it.toNotes() }
    }

    override suspend fun deleteNote(id: Int) {
        notesDao.deleteNote(id)
    }

    override suspend fun editNote(note: Note) {
        notesDao.edit(note.toDbModel())
    }

    override suspend fun addNote(
        title: String,
        description: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val note = Note(0, title, description, false, System.currentTimeMillis())
        val noteDb = note.toDbModel()
        Log.d("NotesRepositoryIml", noteDb.content)
        notesDao.addNote(noteDb)
    }

    override suspend fun switchPinnedStatus(id: Int) {
        notesDao.switchPinnedStatus(id)
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map {it.toNotes() }
    }
}