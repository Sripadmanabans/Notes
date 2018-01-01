package com.sripad.notes.note.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import com.sripad.notes.R
import com.sripad.notes.utils.getFormattedText
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_view_note.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.absoluteValue

class ViewNoteActivity : AppCompatActivity() {

    private val offSetListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        // TODO: Figure out a better way to calculate alpha value.
        val offsetFactor = 1 - (verticalOffset.absoluteValue / 250.0F)
        val alpha = when {
            offsetFactor <= 0 -> 0F
            offsetFactor >= 1 -> 1F
            else -> offsetFactor
        }
        Timber.v("[offSetListener] Offset: $verticalOffset alpha: $alpha")
        view_note_title_container.alpha = alpha
    }

    private lateinit var viewNoteViewModel: ViewNoteViewModel

    @Inject internal lateinit var viewModelProvider: ViewModelProvider.Factory

    private var navigationClickDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_note)

        val noteId = intent?.getLongExtra(EXTRA_NOTE_ID, Long.MIN_VALUE)
        if (noteId == null || noteId == Long.MIN_VALUE) {
            error("Activity not started with a note id: $noteId")
        }

        app_bar_layout.addOnOffsetChangedListener(offSetListener)
        collapsing_toolbar.setExpandedTitleColor(resources.getColor(R.color.transparent, null))
        navigationClickDisposable = toolbar.navigationClicks()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { onBackPressed() }
                .subscribe()

        viewNoteViewModel = viewModelProvider.getViewModel(this)
        viewNoteViewModel.uiModel.observe(this, Observer { model -> model?.let { onUiModelUpdated(it) } })
        viewNoteViewModel.retrieveNote(noteId)
    }

    override fun onDestroy() {
        navigationClickDisposable?.dispose()
        app_bar_layout.removeOnOffsetChangedListener(offSetListener)
        super.onDestroy()
    }

    private fun onUiModelUpdated(viewNoteUiModel: ViewNoteUiModel) {
        when (viewNoteUiModel) {
            is ViewNoteUiModel.NoteLoaded -> {
                val noteInfo = viewNoteUiModel.noteInfo
                view_note_title.text = noteInfo.title
                val modifiedAtText = "Last Updated: ${noteInfo.modifiedOn.getFormattedText()}"
                view_note_updated_at.text = modifiedAtText
                collapsing_toolbar.title = noteInfo.title
                view_note_note.text = noteInfo.note
            }
        }
    }

    companion object {
        private const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"

        fun navigationIntent(context: Context, id: Long): Intent {
            val intent = Intent(context, ViewNoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE_ID, id)
            return intent
        }
    }
}