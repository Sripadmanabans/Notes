package com.sripad.notes.viewmodel

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(modelFactory: ViewModelFactory): ViewModelProvider.Factory
}