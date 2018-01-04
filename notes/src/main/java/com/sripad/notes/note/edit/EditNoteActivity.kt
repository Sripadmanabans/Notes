package com.sripad.notes.note.edit

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import com.sripad.notes.R
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit_note.*
import timber.log.Timber
import javax.inject.Inject

class EditNoteActivity : AppCompatActivity() {

    private val toolbarDisposables = CompositeDisposable()

    private lateinit var editNoteViewModel: EditNoteViewModel

    @Inject internal lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val noteId = intent?.getLongExtra(EXTRA_NOTE_ID, Long.MIN_VALUE)
        if (noteId == null || noteId == Long.MIN_VALUE) {
            error("Activity not started with a note id: $noteId")
        }

        editNoteViewModel = viewModelProvider.getViewModel(this)
        editNoteViewModel.uiModel.observe(this, Observer { model -> model?.let { onUiModelUpdated(it) } })

        toolbar.inflateMenu(R.menu.menu_edit_note)
        val menuItemClickDisposable = toolbar.itemClicks()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    when (it.itemId) {
                        R.id.menu_edit_save -> {
                            val title = title_edit_text.text?.toString().orEmpty()
                            val note = note_edit_text.text?.toString().orEmpty()
                            editNoteViewModel.saveNote(noteId, title, note)
                        }
                        else -> {
                            error("Unknown menu item click.")
                        }
                    }
                }
                .subscribe()

        val navigationClickDisposable = toolbar.navigationClicks()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { onBackPressed() }
                .subscribe()

        toolbarDisposables.addAll(menuItemClickDisposable, navigationClickDisposable)

        editNoteViewModel.retrieveNote(noteId)
    }

    override fun onDestroy() {
        toolbarDisposables.clear()
        super.onDestroy()
    }

    private fun onUiModelUpdated(editNoteUiModel: EditNoteUiModel) {
        Timber.d("[onUiModelUpdated] Ui Model updated: $editNoteUiModel")
        when (editNoteUiModel) {
            is EditNoteUiModel.NoteLoaded -> {
                val noteInfo = editNoteUiModel.noteInfo
                title_edit_text.setText(noteInfo.title, TextView.BufferType.EDITABLE)
                note_edit_text.setText(noteInfo.note, TextView.BufferType.EDITABLE)
            }
            is EditNoteUiModel.NothingToSave -> {
                Toast.makeText(this, R.string.edit_note_empty_toast, Toast.LENGTH_SHORT).show()
            }
            is EditNoteUiModel.NoteSaved -> finish()
        }
    }

    companion object {
        private const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"

        fun navigationIntent(context: Context, id: Long): Intent {
            val intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE_ID, id)
            return intent
        }
    }
}