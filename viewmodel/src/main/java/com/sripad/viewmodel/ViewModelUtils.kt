package com.sripad.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel> ViewModelProvider.Factory.getViewModel(activity: FragmentActivity): T {
    return ViewModelProviders.of(activity, this).get(T::class.java)
}