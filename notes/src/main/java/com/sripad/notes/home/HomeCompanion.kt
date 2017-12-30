package com.sripad.notes.home

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.notes.viewmodel.ConsumerLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

sealed class HomeUiModel {
    object NewNote : HomeUiModel()
}

internal class HomeViewModel @Inject constructor() : ViewModel() {

    private val homeUiRelay = BehaviorRelay.create<HomeUiModel>()
    private val homeUiDisposable: Disposable

    val uiModel = ConsumerLiveData<HomeUiModel>()

    init {
        homeUiDisposable = homeUiRelay.observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
    }

    fun addNewNote() {
        homeUiRelay.accept(HomeUiModel.NewNote)
    }

    override fun onCleared() {
        homeUiDisposable.dispose()
    }
}