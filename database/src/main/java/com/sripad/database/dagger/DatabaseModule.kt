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

@Singleton
@Module
class DatabaseModule {

    @Provides
    internal fun provideNotesDatabase(context: Context): NotesDatabase {
        return Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "notes.db").build()
    }

    @Provides
    internal fun provideNotesDoa(database: NotesDatabase) = database.notesDoa()

    @Provides
    internal fun provideDatabaseAgent(notesDao: NotesDao): DatabaseAgent = DefaultDatabaseAgent(notesDao)
}