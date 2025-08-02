package com.example.notes.domain

import javax.inject.Inject

class SwitchPinnedStatusUseCase @Inject constructor(val repository: NotesRepository) {
    suspend operator fun invoke(id: Int){
        repository.switchPinnedStatus(id)
    }
}