package com.sripad.notes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.sripad.notes.home.HomeViewModel
import com.sripad.notes.note.create.CreateNoteViewModel
import com.sripad.notes.note.edit.EditNoteViewModel
import com.sripad.notes.note.view.ViewNoteViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateNoteViewModel::class)
    abstract fun bindCreateNoteViewModel(createNoteViewModel: CreateNoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ViewNoteViewModel::class)
    abstract fun bindViewNoteViewModel(viewNoteViewModel: ViewNoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditNoteViewModel::class)
    abstract fun bindEditNoteViewModel(editNoteViewModel: EditNoteViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(modelFactory: ViewModelFactory): ViewModelProvider.Factory
}