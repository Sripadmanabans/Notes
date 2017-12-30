package com.sripad.notes.home

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

internal interface HomePresenter {

    fun uiModel(): Observable<HomeUiModel>

    fun addNewNote()
}

sealed class HomeUiModel {
    object NewNote : HomeUiModel()
}

internal class DefaultHomePresenter : HomePresenter {

    private val menuClickRelay = BehaviorRelay.create<HomeUiModel>()

    override fun uiModel(): Observable<HomeUiModel> = menuClickRelay

    override fun addNewNote() {
        menuClickRelay.accept(HomeUiModel.NewNote)
    }
}