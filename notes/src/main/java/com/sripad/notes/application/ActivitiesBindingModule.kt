package com.sripad.notes.application

import com.sripad.notes.home.HomeActivity
import com.sripad.notes.home.HomeModule
import com.sripad.notes.note.NewNoteActivity
import com.sripad.notes.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivitiesBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeSplashActivityInjector(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [HomeModule::class])
    abstract fun contributeHomeActivityInjector(): HomeActivity

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeNewNoteActivityInjector(): NewNoteActivity
}