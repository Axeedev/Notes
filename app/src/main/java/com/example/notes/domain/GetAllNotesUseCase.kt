package com.example.notes.domain

import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(val repository: NotesRepository)
{
    operator fun invoke(): Flow<List<Note>>{
        return repository.getAllNotes()
    }
}