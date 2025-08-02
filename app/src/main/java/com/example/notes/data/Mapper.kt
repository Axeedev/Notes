package com.example.notes.data

import android.util.Log
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel{

    Log.d("NotesRepositoryIml", "HERE")
    val contentAsString = Json.encodeToString(content.toContentItemDbModels())
    Log.d("NotesRepositoryIml", "content: $contentAsString")
    return NoteDbModel(
        id = this.id,
        title = this.title,
        content = contentAsString,
        updatedAt = this.updatedAt,
        isPinned = this.isPinned
    )
}

fun List<ContentItem>.toContentItemDbModels() : List<ContentItemDbModel>{
    Log.d("NotesRepositoryIml", "AAAAAAAAAAAA")
    return map { contentItem ->
        Log.d("NotesRepositoryIml", contentItem.toString())
        when(contentItem){
            is ContentItem.ContentItemImage ->{
                ContentItemDbModel.ContentItemImageDb(url = contentItem.url)
            }
            is ContentItem.ContentItemText ->{
                Log.d("NotesRepositoryIml", contentItem.text)
                ContentItemDbModel.ContentItemTextDb(text = contentItem.text)
            }
        }
    }


}
fun List<ContentItemDbModel>.toContentItems() : List<ContentItem>{

    return this.map { contentItem ->
        when(contentItem){
            is ContentItemDbModel.ContentItemImageDb ->{
                ContentItem.ContentItemImage(url = contentItem.url)
            }
            is ContentItemDbModel.ContentItemTextDb ->{
                ContentItem.ContentItemText(text = contentItem.text)
            }
        }
    }
}

fun NoteDbModel.toNote() : Note {

    val content = Json.decodeFromString<List<ContentItemDbModel>>(content)

    return Note(
        id = this.id,
        title = this.title,
        content = content.toContentItems(),
        updatedAt = this.updatedAt,
        isPinned = this.isPinned
    )
}


fun List<NoteDbModel>.toNotes(): List<Note>{
    return this.map { it.toNote() }
}
fun List<Note>.toDbNotes(): List<NoteDbModel>{
    return this.map { it.toDbModel() }
}