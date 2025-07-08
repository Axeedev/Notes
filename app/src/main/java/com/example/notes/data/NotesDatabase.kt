package com.example.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities =[NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao() : NotesDao

    companion object{
        private var instance: NotesDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): NotesDatabase{
            instance?.let { return it }
            synchronized(lock) {
                instance?.let { return it }
                return Room.databaseBuilder(
                    context,
                    klass = NotesDatabase::class.java,
                    name = "notes.db"
                    ).build().also { instance = it }
            }
        }
    }
}