package com.example.notes.data

import com.example.notes.domain.Note

fun Note.toDbModel(): NoteDbModel{
    return NoteDbModel(
        id = this.id,
        title = this.title,
        content = this.content,
        updatedAt = this.updatedAt,
        isPinned = this.isPinned
    )
}

fun NoteDbModel.toNote() : Note = Note(
    id = this.id,
    title = this.title,
    content = this.content,
    updatedAt = this.updatedAt,
    isPinned = this.isPinned
)

fun List<NoteDbModel>.toNotes(): List<Note>{
    return this.map { it.toNote() }
}
fun List<Note>.toDbNotes(): List<NoteDbModel>{
    return this.map { it.toDbModel() }
}