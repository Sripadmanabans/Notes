package com.sripad.notes.note.view

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.database.agent.DatabaseAgent
import com.sripad.database.agent.NoteInfo
import com.sripad.notes.viewmodel.ConsumerLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

sealed class ViewNoteUiModel {
    data class NoteLoaded(val noteInfo: NoteInfo) : ViewNoteUiModel()
}

class ViewNoteViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val viewNoteUiModelRelay = BehaviorRelay.create<ViewNoteUiModel>()
    private val viewNoteUiModelDisposable: Disposable

    private var retrieveNoteDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<ViewNoteUiModel>()

    init {
        viewNoteUiModelDisposable = viewNoteUiModelRelay.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
    }

    fun retrieveNote(noteId: Long) {
        retrieveNoteDisposable?.dispose()
        retrieveNoteDisposable = databaseAgent.retrieveNote(noteId)
                .doOnSuccess { viewNoteUiModelRelay.accept(ViewNoteUiModel.NoteLoaded(it)) }
                .subscribe()
    }

    override fun onCleared() {
        retrieveNoteDisposable?.dispose()
        viewNoteUiModelDisposable.dispose()
    }
}