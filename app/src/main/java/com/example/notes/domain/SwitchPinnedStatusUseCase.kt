package com.example.notes.domain

class SwitchPinnedStatusUseCase(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int){
        repository.switchPinnedStatus(id)
    }
}