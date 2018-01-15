package com.sripad.notes.note.edit

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.database.agent.DatabaseAgent
import com.sripad.database.agent.NoteInfo
import com.sripad.viewmodel.ConsumerLiveData
import com.sripad.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@Module
internal abstract class EditNoteViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(EditNoteViewModel::class)
    abstract fun bindEditNoteViewModel(editNoteViewModel: EditNoteViewModel): ViewModel
}

sealed class EditNoteUiModel {
    data class NoteLoaded(val noteInfo: NoteInfo) : EditNoteUiModel()
    object NothingToSave : EditNoteUiModel()
    object NoteSaved : EditNoteUiModel()
}

class EditNoteViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val editNoteRelay = BehaviorRelay.create<EditNoteUiModel>()

    private val disposable: Disposable

    private var saveNoteDisposable: Disposable? = null
    private var retrieveNoteDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<EditNoteUiModel>()

    init {
        disposable = editNoteRelay.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
    }

    fun saveNote(noteId: Long, title: String, note: String) {
        if (title.isBlank() && note.isBlank()) {
            editNoteRelay.accept(EditNoteUiModel.NothingToSave)
        } else {
            saveNoteDisposable?.dispose()
            saveNoteDisposable = databaseAgent.updateNote(noteId, title, note)
                    .doOnSuccess { editNoteRelay.accept(EditNoteUiModel.NoteSaved) }
                    .subscribe()
        }
    }

    fun retrieveNote(noteId: Long) {
        retrieveNoteDisposable = databaseAgent.retrieveNote(noteId)
                .doOnSuccess { editNoteRelay.accept(EditNoteUiModel.NoteLoaded(it)) }
                .subscribe()
    }

    override fun onCleared() {
        saveNoteDisposable?.dispose()
        retrieveNoteDisposable?.dispose()
        disposable.dispose()
    }
}

