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
    data class Notes(val value: List<NoteInfo>) : HomeUiModel()
    object ShowLoadingSpinner : HomeUiModel()
    data class EmptyList(val afterFilter: Boolean) : HomeUiModel()
}

internal class HomeViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val homeUiRelay = BehaviorRelay.create<HomeUiModel>()
    private val homeUiDisposable: Disposable

    private var loadNotesDisposable: Disposable? = null

    private var toggleFavoriteDisposable: Disposable? = null
    private var toggleStarredDisposable: Disposable? = null

    private var deleteDisposable: Disposable? = null

    private var retrieveFavoriteNotesDisposable: Disposable? = null
    private var retrieveStarredNotesDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<HomeUiModel>()

    init {
        homeUiDisposable = homeUiRelay.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
        retrieveNotes()
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

    fun deleteNote(noteInfo: NoteInfo) {
        deleteDisposable?.dispose()
        deleteDisposable = databaseAgent.deleteNote(noteInfo)
                .doOnSuccess { Timber.d("[deleteNote] Deleted $it successful") }
                .subscribe()
    }

    fun retrieveFavoriteNotes() {
        retrieveFavoriteNotesDisposable?.dispose()
        retrieveFavoriteNotesDisposable = databaseAgent.retrieveFavoriteNotes().toObservable()
                .doOnNext { Timber.d("[retrieveFavoriteNotes] Notes retrieved: $it") }
                .map {
                    if (it.isEmpty()) {
                        HomeUiModel.EmptyList(true)
                    } else {
                        HomeUiModel.Notes(it)
                    }
                }
                .startWith(HomeUiModel.ShowLoadingSpinner)
                .subscribe { homeUiRelay.accept(it) }
    }

    fun retrieveStarredNotes() {
        retrieveStarredNotesDisposable?.dispose()
        retrieveStarredNotesDisposable = databaseAgent.retrieveStarredNotes().toObservable()
                .map { it.filter { noteInfo -> noteInfo.starred } }
                .doOnNext { Timber.d("[retrieveStarredNotes] Notes retrieved: $it") }
                .map {
                    if (it.isEmpty()) {
                        HomeUiModel.EmptyList(true)
                    } else {
                        HomeUiModel.Notes(it)
                    }
                }
                .startWith(HomeUiModel.ShowLoadingSpinner)
                .subscribe { homeUiRelay.accept(it) }
    }

    fun retrieveNotes() {
        loadNotesDisposable?.dispose()
        loadNotesDisposable = databaseAgent.retrieveNotes()
                .map {
                    if (it.isEmpty()) {
                        HomeUiModel.EmptyList(false)
                    } else {
                        HomeUiModel.Notes(it)
                    }
                }
                .startWith(HomeUiModel.ShowLoadingSpinner)
                .subscribe { homeUiRelay.accept(it) }
    }

    override fun onCleared() {
        toggleFavoriteDisposable?.dispose()
        toggleStarredDisposable?.dispose()
        deleteDisposable?.dispose()

        retrieveFavoriteNotesDisposable?.dispose()
        retrieveStarredNotesDisposable?.dispose()

        loadNotesDisposable?.dispose()

        homeUiDisposable.dispose()
    }
}