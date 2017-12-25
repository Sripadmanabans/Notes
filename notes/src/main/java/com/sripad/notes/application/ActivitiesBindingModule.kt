package com.sripad.notes.application

import com.sripad.notes.home.HomeActivity
import com.sripad.notes.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeSplashActivityInjector(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun contributeHomeActivityInjector(): HomeActivity
}