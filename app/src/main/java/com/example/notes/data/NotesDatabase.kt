package com.example.notes.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =[NoteDbModel::class],
    version = 2,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao() : NotesDao


}