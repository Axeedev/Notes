package com.example.notes.domain

class DeleteNoteUseCase(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int){
        repository.deleteNote(id)
    }
}