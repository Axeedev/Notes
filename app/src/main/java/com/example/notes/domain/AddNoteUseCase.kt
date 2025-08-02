package com.example.notes.domain

import javax.inject.Inject

class AddNoteUseCase @Inject constructor(val repository: NotesRepository) {
    suspend operator fun invoke(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ){
        repository.addNote(title, content, updatedAt = updatedAt, isPinned = isPinned)
    }
}