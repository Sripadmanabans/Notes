package com.sripad.database.dagger

import android.arch.persistence.room.Room
import android.content.Context
import com.sripad.database.NotesDatabase
import com.sripad.database.agent.DatabaseAgent
import com.sripad.database.agent.DefaultDatabaseAgent
import com.sripad.database.dao.NotesDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// TODO: Figure out why Singleton cannot be pulled to module level.
@Module
class DatabaseModule {

    @Singleton
    @Provides
    internal fun provideNotesDatabase(context: Context): NotesDatabase {
        return Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "notes.db").build()
    }

    @Singleton
    @Provides
    internal fun provideNotesDoa(database: NotesDatabase) = database.notesDoa()

    @Singleton
    @Provides
    internal fun provideDatabaseAgent(notesDao: NotesDao): DatabaseAgent = DefaultDatabaseAgent(notesDao)
}