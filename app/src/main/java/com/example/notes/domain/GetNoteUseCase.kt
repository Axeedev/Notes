package com.example.notes.domain

import javax.inject.Inject

class GetNoteUseCase @Inject constructor(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int):Note{
        return repository.getNote(id)
    }
}