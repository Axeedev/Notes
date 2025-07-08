package com.example.notes.data

import android.content.Context
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class NotesRepositoryIml private constructor(context: Context) : NotesRepository {

    private val notesDatabase = NotesDatabase.getInstance(context)
    private val notesDao = notesDatabase.notesDao()

    override suspend fun getNote(id: Int): Note {
        return notesDao.getNodeById(id).toNote()
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getNotes().map { it.toNotes()
        }
    }

    override suspend fun deleteNote(id: Int) {
        notesDao.deleteNote(id)
    }

    override suspend fun editNote(note: Note) {
        notesDao.edit(note.toDbModel())
    }

    override suspend fun addNote(title: String, description: String, isPinned: Boolean, updatedAt: Long) {
        val note = NoteDbModel(0, title = title, content = description,updatedAt, isPinned )
        notesDao.addNote(note)
    }

    override suspend fun switchPinnedStatus(id: Int) {
        notesDao.switchPinnedStatus(id)
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map {it.toNotes() }
    }
    companion object{
        private val lock = Any()
        private var instance : NotesRepositoryIml? = null
        fun getInstance(context: Context) : NotesRepositoryIml{
            instance?.let { return it }

            synchronized (lock){
                instance?.let { return it }
                return NotesRepositoryIml(context).also { instance = it }
            }
        }
    }
}