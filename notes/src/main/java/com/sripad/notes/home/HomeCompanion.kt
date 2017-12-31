package com.sripad.notes.home

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.database.agent.DatabaseAgent
import com.sripad.database.agent.NoteInfo
import com.sripad.notes.viewmodel.ConsumerLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

sealed class HomeUiModel {
    data class AllNotes(val notes: List<NoteInfo>) : HomeUiModel()
    object ShowLoadingSpinner : HomeUiModel()
    object Empty : HomeUiModel()
}

internal class HomeViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val homeUiRelay = BehaviorRelay.create<HomeUiModel>()
    private val homeUiDisposable: Disposable

    private val loadNotesDisposable: Disposable

    private var toggleFavoriteDisposable: Disposable? = null
    private var toggleStarredDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<HomeUiModel>()

    init {
        homeUiDisposable = homeUiRelay.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
        loadNotesDisposable = databaseAgent.retrieveNotes()
                .map {
                    if (it.isEmpty()) {
                        HomeUiModel.Empty
                    } else {
                        HomeUiModel.AllNotes(it)
                    }
                }
                .startWith(HomeUiModel.ShowLoadingSpinner)
                .subscribe { homeUiRelay.accept(it) }
    }

    fun toggleFavorite(noteInfo: NoteInfo) {
        toggleFavoriteDisposable?.dispose()
        toggleFavoriteDisposable = databaseAgent.toggleFavoriteStatus(noteInfo, !noteInfo.favorite)
                .doOnSuccess { Timber.d("[toggleFavorite] Updated $it successful") }
                .subscribe()
    }

    fun toggleStarred(noteInfo: NoteInfo) {
        toggleStarredDisposable?.dispose()
        toggleStarredDisposable = databaseAgent.toggleStarredStatus(noteInfo, !noteInfo.starred)
                .doOnSuccess { Timber.d("[toggleStarred] Updated $it successful") }
                .subscribe()
    }

    override fun onCleared() {
        toggleFavoriteDisposable?.dispose()
        toggleStarredDisposable?.dispose()
        loadNotesDisposable.dispose()
        homeUiDisposable.dispose()
    }
}