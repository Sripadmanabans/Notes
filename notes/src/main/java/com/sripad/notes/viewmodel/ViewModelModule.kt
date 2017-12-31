package com.sripad.notes.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.sripad.notes.application.ActivityScoped
import com.sripad.notes.home.HomeViewModel
import com.sripad.notes.note.NewNoteViewModel
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
    @ViewModelKey(NewNoteViewModel::class)
    abstract fun bindNewNoteViewModel(newNoteViewModel: NewNoteViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(modelFactory: ViewModelFactory): ViewModelProvider.Factory
}