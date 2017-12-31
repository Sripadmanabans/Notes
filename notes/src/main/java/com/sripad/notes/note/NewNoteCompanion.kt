package com.sripad.notes.note

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.database.agent.DatabaseAgent
import com.sripad.notes.viewmodel.ConsumerLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

internal sealed class NewNoteUiModel {
    data class ValueSaved(val id: Long) : NewNoteUiModel()
    object NothingToSave : NewNoteUiModel()
    object BackPressed: NewNoteUiModel()
}

internal class NewNoteViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val newNoteUiRelay = BehaviorRelay.create<NewNoteUiModel>()

    private val newNoteUiModelDisposable: Disposable

    private var saveToDatabaseDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<NewNoteUiModel>()

    init {
        newNoteUiModelDisposable = newNoteUiRelay.observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
    }

    fun save(title: String, note: String) {
        if (title.isBlank() && note.isBlank()) {
            newNoteUiRelay.accept(NewNoteUiModel.NothingToSave)
        } else {
            saveToDatabaseDisposable = databaseAgent.insertNote(title, note)
                    .doOnSuccess { newNoteUiRelay.accept(NewNoteUiModel.ValueSaved(it)) }
                    .subscribe()
        }
    }

    fun onBackPressed() {
        newNoteUiRelay.accept(NewNoteUiModel.BackPressed)
    }

    override fun onCleared() {
        saveToDatabaseDisposable?.dispose()
        newNoteUiModelDisposable.dispose()
    }
}