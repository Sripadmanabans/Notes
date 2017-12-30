package com.sripad.notes.application

import android.content.Context
import com.sripad.database.dagger.DatabaseModule
import com.sripad.notes.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidInjectionModule::class,
            ActivitiesBindingModule::class,
            DatabaseModule::class,
            ViewModelModule::class
        ]
)
interface NotesApplicationComponent : AndroidInjector<NotesApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): NotesApplicationComponent
    }
}