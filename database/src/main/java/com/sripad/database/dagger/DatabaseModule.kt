package com.sripad.database.dagger

import android.arch.persistence.room.Room
import android.content.Context
import com.sripad.database.NotesDatabase
import com.sripad.database.agent.DatabaseAgent
import com.sripad.database.agent.DefaultDatabaseAgent
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @JvmStatic
    @Singleton
    @Provides
    internal fun provideNotesDatabase(context: Context): NotesDatabase {
        return Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "notes.db").build()
    }

    @JvmStatic
    @Singleton
    @Provides
    internal fun provideNotesDoa(database: NotesDatabase) = database.notesDao()
}

@Module
abstract class DatabaseAgentModule {

    @Binds
    internal abstract fun bindDatabaseAgent(databaseAgent: DefaultDatabaseAgent): DatabaseAgent
}