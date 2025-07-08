package com.example.notes.domain

class GetNoteUseCase(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int):Note{
        return repository.getNote(id)
    }
}