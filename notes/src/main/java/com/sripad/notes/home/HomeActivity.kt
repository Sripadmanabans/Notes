package com.sripad.notes.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.sripad.database.agent.NoteInfo
import com.sripad.notes.R
import com.sripad.notes.note.create.CreateNoteActivity
import com.sripad.notes.utils.getFormattedText
import com.sripad.notes.utils.makeGone
import com.sripad.notes.utils.makeVisible
import com.sripad.notes.utils.setTextOrGone
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.recycler_note_item.view.*
import timber.log.Timber
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private val notesAdapter = NotesAdapter(this::itemClick)

    private lateinit var homeViewModel: HomeViewModel

    @Inject internal lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeViewModel = viewModelFactory.getViewModel(this)
        homeViewModel.uiModel.observe(this, Observer { model -> model?.let { onUiModelUpdated(it) } })

        toolbar.inflateMenu(R.menu.menu_home)
        val itemClickDisposables = toolbar.itemClicks()
                .doOnNext {
                    when (it.itemId) {
                        R.id.menu_add_note -> startActivity(CreateNoteActivity.navigationIntent(this))
                        else -> error("Unhandled menu item ($it) click")
                    }
                }
                .subscribe()

        disposables.addAll(itemClickDisposables)

        notes_list.adapter = notesAdapter
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun onUiModelUpdated(homeUiModel: HomeUiModel) {
        Timber.d("[onUiModelUpdated] Ui Model Updated: $homeUiModel")
        when (homeUiModel) {
            is HomeUiModel.AllNotes -> {
                notes_list.makeVisible()
                notesAdapter.updateList(homeUiModel.notes)
                loading_spinner.makeGone()
                empty_list_text.makeGone()
            }
            is HomeUiModel.ShowLoadingSpinner -> {
                loading_spinner.makeVisible()
                notes_list.makeGone()
                empty_list_text.makeGone()
            }
            is HomeUiModel.Empty -> {
                empty_list_text.makeVisible()
                loading_spinner.makeGone()
                notes_list.makeGone()
            }
        }
    }

    private fun itemClick(@IdRes id: Int, noteInfo: NoteInfo) {
        when (id) {
            R.id.favorite -> homeViewModel.toggleFavorite(noteInfo)
            R.id.star -> homeViewModel.toggleStarred(noteInfo)
            else -> error("Click not handled for id: $id")
        }
    }
}

private class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindNoteInfo(noteInfo: NoteInfo, itemClick: (Int, NoteInfo) -> Unit) {
        itemView.title.setTextOrGone(noteInfo.title)
        itemView.gist.setTextOrGone(noteInfo.note)
        itemView.modified_on.setTextOrGone(noteInfo.modifiedOn.getFormattedText())

        @DrawableRes val favoriteDrawableId = if (noteInfo.favorite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite_unselected
        itemView.favorite.setBackgroundResource(favoriteDrawableId)
        itemView.favorite.setOnClickListener { itemClick(it.id, noteInfo) }

        @DrawableRes val starDrawableId = if (noteInfo.starred) R.drawable.ic_star_selected else R.drawable.ic_star_unselected
        itemView.star.setBackgroundResource(starDrawableId)
        itemView.star.setOnClickListener { itemClick(it.id, noteInfo) }
    }
}

private class NotesAdapter(private val itemClick: (Int, NoteInfo) -> Unit) : RecyclerView.Adapter<NoteViewHolder>() {

    private val notes = mutableListOf<NoteInfo>()

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bindNoteInfo(notes[position], itemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount() = notes.size

    fun updateList(notes: List<NoteInfo>) {
        val notesDiffUtil = NotesDiffUtil(this.notes, notes)
        val calculateDiff = DiffUtil.calculateDiff(notesDiffUtil)

        this.notes.clear()
        this.notes.addAll(notes)
        calculateDiff.dispatchUpdatesTo(this)
    }
}

private class NotesDiffUtil(private val oldList: List<NoteInfo>, private val newList: List<NoteInfo>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}