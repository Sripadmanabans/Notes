package com.sripad.notes.home

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class HomeActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeFilterDialogFragmentInjector(): FilterDialogFragment
}