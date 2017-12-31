package com.sripad.notes.note.create

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import com.sripad.notes.R
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_note.*
import timber.log.Timber
import javax.inject.Inject

class CreateNoteActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var createNoteViewModel: CreateNoteViewModel

    @Inject internal lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        createNoteViewModel = viewModelProvider.getViewModel(this)
        createNoteViewModel.uiModel.observe(this, Observer { model -> model?.let { onUiModelUpdated(it) } })

        toolbar.inflateMenu(R.menu.menu_create_note)
        val itemClickDisposable = toolbar.itemClicks()
                .map {
                    when (it.itemId) {
                        R.id.menu_save -> {
                            val title = title_edit_text.text?.toString().orEmpty()
                            val note = note_edit_text.text?.toString().orEmpty()
                            createNoteViewModel.save(title, note)
                        }
                        else -> {
                            error("Unhandled menu item clicks.")
                        }
                    }
                }
                .subscribe()

        val navigationDisposable = toolbar.navigationClicks().observeOn(AndroidSchedulers.mainThread()).subscribe { onBackPressed() }

        disposables.addAll(navigationDisposable, itemClickDisposable)
    }

    private fun onUiModelUpdated(createNoteUiModel: CreateNoteUiModel) {
        Timber.d("[onUiModelUpdated] Ui Model updated: $createNoteUiModel")
        when (createNoteUiModel) {
            is CreateNoteUiModel.NothingToSave -> {
                Toast.makeText(this, R.string.new_note_empty_toast, Toast.LENGTH_SHORT).show()
            }
            is CreateNoteUiModel.ValueSaved -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    companion object {
        fun navigationIntent(context: Context): Intent {
            return Intent(context, CreateNoteActivity::class.java)
        }
    }
}