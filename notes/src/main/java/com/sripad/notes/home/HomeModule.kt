package com.sripad.notes.home

import com.sripad.notes.application.ActivityScoped
import dagger.Module
import dagger.Provides

@ActivityScoped
@Module
internal class HomeModule {

    @Provides
    fun provideHomePresenter(): HomePresenter = DefaultHomePresenter()
}