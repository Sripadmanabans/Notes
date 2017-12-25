package com.sripad.notes.application

import android.app.Activity
import android.app.Application
import com.sripad.notes.timber.CustomTimberTree
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class NotesApplication : Application(), HasActivityInjector {

    @Inject internal lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        Timber.plant(CustomTimberTree())

        val applicationComponent = DaggerNotesApplicationComponent.builder().bindContext(this).build()
        applicationComponent.inject(this)
    }

    override fun activityInjector() = androidInjector
}