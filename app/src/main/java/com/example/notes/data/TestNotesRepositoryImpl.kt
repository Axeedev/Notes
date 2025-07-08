//package com.example.notes.data
//
//import com.example.notes.domain.Note
//import com.example.notes.domain.NotesRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.update
//
//object TestNotesRepositoryImpl : NotesRepository {
//
//    private val testData = mutableListOf<Note>().apply {
//        repeat (10){
//            add(Note(it, "title$it", "description$it",false, System.currentTimeMillis()))
//        }
//    }
//    private val notesListFlow  =  MutableStateFlow<List<Note>>(testData)
//
//    override suspend fun getNote(id: Int): Note {
//        return notesListFlow.value.first { it.id == id }
//    }
//
//    override fun getAllNotes(): Flow<List<Note>> {
//        return notesListFlow.asStateFlow()
//    }
//
//    override suspend fun deleteNote(id: Int) {
//        notesListFlow.update {
//            it.toMutableList().apply {
//                removeIf{note ->
//                    note.id == id
//
//                }
//            }
//        }
//    }
//
//    override suspend fun editNote(note: Note) {
//        notesListFlow.update {
//            it.map { oldNote ->
//                if (note.id == oldNote.id){
//                    note
//                }
//                else{
//                    oldNote
//                }
//            }
//        }
//    }
//
//    override suspend fun addNote(title: String, description: String) {
//        notesListFlow.update {
//            val new = Note(
//                id = it.size,
//                title = title,
//                content = description,
//                updatedAt = System.currentTimeMillis(),
//                isPinned = false
//            )
//            it + new
//        }
//    }
//
//
//
//    override suspend fun switchPinnedStatus(id: Int) {
//        notesListFlow.update {
//            it.map {old ->
//                if (id == old.id){
//                    old.copy(isPinned = !old.isPinned)
//                }
//                else old
//            }
//        }
//    }
//
//    override fun searchNotes(query: String): Flow<List<Note>> {
//        return notesListFlow.map {current ->
//            current.filter {
//                it.content.contains(query) || it.title.contains(query)
//            }
//        }
//    }
//}