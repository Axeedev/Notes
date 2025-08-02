package com.example.notes.domain

import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int){
        repository.deleteNote(id)
    }
}