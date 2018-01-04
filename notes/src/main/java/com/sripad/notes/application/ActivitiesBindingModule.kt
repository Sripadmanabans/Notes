package com.sripad.notes.application

import com.sripad.notes.home.HomeActivity
import com.sripad.notes.home.HomeActivityModule
import com.sripad.notes.note.create.CreateNoteActivity
import com.sripad.notes.note.edit.EditNoteActivity
import com.sripad.notes.note.view.ViewNoteActivity
import com.sripad.notes.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivitiesBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeSplashActivityInjector(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [HomeActivityModule::class])
    abstract fun contributeHomeActivityInjector(): HomeActivity

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeCreateNoteActivityInjector(): CreateNoteActivity

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeViewNoteActivityInjector(): ViewNoteActivity

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeEditNoteActivityInjector(): EditNoteActivity
}