package com.example.notes.domain

class AddNoteUseCase(val repository: NotesRepository) {
    suspend operator fun invoke(title: String, content: String, isPinned: Boolean, updatedAt: Long){
        repository.addNote(title, content, updatedAt = updatedAt, isPinned = isPinned)
    }
}