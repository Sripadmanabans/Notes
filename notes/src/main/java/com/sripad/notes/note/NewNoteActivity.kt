package com.sripad.notes.note

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import com.sripad.notes.R
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_new_note.*
import timber.log.Timber
import javax.inject.Inject

class NewNoteActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var newNoteViewModel: NewNoteViewModel

    @Inject internal lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        newNoteViewModel = viewModelProvider.getViewModel(this)
        newNoteViewModel.uiModel.observe(this, Observer { model -> model?.let { onUiModelUpdated(it) } })

        toolbar.inflateMenu(R.menu.menu_new_note)
        val itemClickDisposable = toolbar.itemClicks()
                .map {
                    when (it.itemId) {
                        R.id.menu_save -> {
                            val title = title_edit_text.text?.toString().orEmpty()
                            val note = note_edit_text.text?.toString().orEmpty()
                            newNoteViewModel.save(title, note)
                        }
                        else -> {
                            error("Unhandled menu item clicks.")
                        }
                    }
                }
                .subscribe()

        val navigationDisposable = toolbar.navigationClicks().subscribe { newNoteViewModel.onBackPressed() }

        disposables.addAll(navigationDisposable, itemClickDisposable)
    }

    private fun onUiModelUpdated(newNoteUiModel: NewNoteUiModel) {
        Timber.d("[onUiModelUpdated] Ui Model updated: $newNoteUiModel")
        when (newNoteUiModel) {
            is NewNoteUiModel.NothingToSave -> {
                Toast.makeText(this, R.string.new_note_empty_toast, Toast.LENGTH_SHORT).show()
            }
            is NewNoteUiModel.ValueSaved -> {
                finish()
            }
            is NewNoteUiModel.BackPressed -> {
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}