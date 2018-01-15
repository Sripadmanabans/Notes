package com.sripad.notes.viewmodel

import android.arch.lifecycle.ViewModelProvider
import com.sripad.notes.home.HomeViewModelModule
import com.sripad.notes.note.create.CreateNoteViewModelModule
import com.sripad.notes.note.edit.EditNoteViewModelModule
import com.sripad.notes.note.view.ViewNoteViewModelModule
import com.sripad.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module(
        includes = [
            HomeViewModelModule::class,
            CreateNoteViewModelModule::class,
            EditNoteViewModelModule::class,
            ViewNoteViewModelModule::class
        ]
)
internal abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(modelFactory: ViewModelFactory): ViewModelProvider.Factory
}