package com.example.notes.domain

class EditNoteUseCase(val repository: NotesRepository) {
    suspend operator fun invoke(note: Note) {
        repository.editNote(note)
    }
}