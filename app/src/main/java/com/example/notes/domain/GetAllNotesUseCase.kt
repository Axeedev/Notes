package com.example.notes.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(val repository: NotesRepository)
{
    operator fun invoke(): Flow<List<Note>>{
        return repository.getAllNotes()
    }
}