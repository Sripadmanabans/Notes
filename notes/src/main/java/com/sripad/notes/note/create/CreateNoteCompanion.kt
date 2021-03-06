package com.sripad.notes.note.create

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sripad.database.agent.DatabaseAgent
import com.sripad.viewmodel.ConsumerLiveData
import com.sripad.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@Module
internal abstract class CreateNoteViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreateNoteViewModel::class)
    abstract fun bindCreateNoteViewModel(createNoteViewModel: CreateNoteViewModel): ViewModel
}

internal sealed class CreateNoteUiModel {
    data class ValueSaved(val id: Long) : CreateNoteUiModel()
    object NothingToSave : CreateNoteUiModel()
}

internal class CreateNoteViewModel @Inject constructor(private val databaseAgent: DatabaseAgent) : ViewModel() {

    private val createNoteUiRelay = BehaviorRelay.create<CreateNoteUiModel>()

    private val createNoteUiModelDisposable: Disposable

    private var saveToDatabaseDisposable: Disposable? = null

    val uiModel = ConsumerLiveData<CreateNoteUiModel>()

    init {
        createNoteUiModelDisposable = createNoteUiRelay.observeOn(AndroidSchedulers.mainThread()).subscribe(uiModel)
    }

    fun save(title: String, note: String) {
        if (title.isBlank() && note.isBlank()) {
            createNoteUiRelay.accept(CreateNoteUiModel.NothingToSave)
        } else {
            saveToDatabaseDisposable = databaseAgent.insertNote(title, note)
                    .doOnSuccess { createNoteUiRelay.accept(CreateNoteUiModel.ValueSaved(it)) }
                    .subscribe()
        }
    }

    override fun onCleared() {
        saveToDatabaseDisposable?.dispose()
        createNoteUiModelDisposable.dispose()
    }
}