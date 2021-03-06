package com.sripad.notes.home

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.sripad.database.agent.NoteInfo
import com.sripad.notes.R
import com.sripad.notes.note.create.CreateNoteActivity
import com.sripad.notes.note.view.ViewNoteActivity
import com.sripad.notes.utils.getFormattedText
import com.sripad.notes.utils.makeGone
import com.sripad.notes.utils.makeVisible
import com.sripad.notes.utils.setTextOrGone
import com.sripad.viewmodel.getViewModel
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.empty_list_text
import kotlinx.android.synthetic.main.activity_home.loading_spinner
import kotlinx.android.synthetic.main.activity_home.notes_list
import kotlinx.android.synthetic.main.activity_home.toolbar
import kotlinx.android.synthetic.main.recycler_note_item.view.favorite
import kotlinx.android.synthetic.main.recycler_note_item.view.gist
import kotlinx.android.synthetic.main.recycler_note_item.view.modified_on
import kotlinx.android.synthetic.main.recycler_note_item.view.star
import kotlinx.android.synthetic.main.recycler_note_item.view.title
import timber.log.Timber
import javax.inject.Inject

internal class HomeActivity : DaggerAppCompatActivity() {

    private val toolbarDisposables = CompositeDisposable()
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
                        R.id.menu_filter_list -> FilterDialogFragment().show(supportFragmentManager, "FilterDialog")
                        else -> error("Unhandled menu item ($it) click")
                    }
                }
                .subscribe()

        toolbarDisposables.addAll(itemClickDisposables)

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        notes_list.apply {
            addItemDecoration(dividerItemDecoration)
            adapter = notesAdapter
        }

        val deleteDrawable = getDrawable(R.drawable.ic_delete)
        val backgroundColor = getColor(R.color.home_swipe_delete_background)
        val deleteTextColor = getColor(R.color.home_swipe_delete_text_color)
        val deleteTextMarginX = resources.displayMetrics.density * 24
        val deleteTextSize = resources.displayMetrics.scaledDensity * 14
        val config = SwipeCallbackConfig(deleteDrawable, deleteTextSize, deleteTextMarginX, backgroundColor, deleteTextColor)
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(config) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteInfo = notesAdapter.getItem(viewHolder.adapterPosition)
                homeViewModel.deleteNote(noteInfo)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(notes_list)
    }

    override fun onDestroy() {
        toolbarDisposables.clear()
        super.onDestroy()
    }

    private fun onUiModelUpdated(homeUiModel: HomeUiModel) {
        Timber.d("[onUiModelUpdated] Ui Model Updated: $homeUiModel")
        when (homeUiModel) {
            is HomeUiModel.Notes -> {
                notes_list.makeVisible()
                notesAdapter.updateList(homeUiModel.value)
                loading_spinner.makeGone()
                empty_list_text.makeGone()
            }
            is HomeUiModel.ShowLoadingSpinner -> {
                loading_spinner.makeVisible()
                notes_list.makeGone()
                empty_list_text.makeGone()
            }
            is HomeUiModel.EmptyList -> {
                @StringRes val emptyTextId = if (homeUiModel.afterFilter) {
                    R.string.empty_filtered_list_text
                } else {
                    R.string.empty_list_text
                }
                empty_list_text.apply {
                    setText(emptyTextId)
                    makeVisible()
                }
                loading_spinner.makeGone()
                notes_list.makeGone()
            }
        }
    }

    private fun itemClick(@IdRes id: Int, noteInfo: NoteInfo) {
        when (id) {
            R.id.favorite -> homeViewModel.toggleFavorite(noteInfo)
            R.id.star -> homeViewModel.toggleStarred(noteInfo)
            R.id.note_item -> startActivity(ViewNoteActivity.navigationIntent(this, noteInfo.id))
            else -> error("Click not handled for id: $id")
        }
    }
}

private class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindNoteInfo(noteInfo: NoteInfo, itemClick: (Int, NoteInfo) -> Unit) {
        itemView.setOnClickListener { itemClick(it.id, noteInfo) }
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

    fun getItem(position: Int) = notes[position]

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

private data class SwipeCallbackConfig(
        val deleteDrawable: Drawable,
        val deleteTextSize: Float,
        val deleteTextMarginX: Float,
        @ColorInt val backgroundColor: Int,
        @ColorInt val textColor: Int
)

private abstract class SwipeToDeleteCallback(config: SwipeCallbackConfig) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val background = ColorDrawable(config.backgroundColor)

    private val deleteTextPaint = Paint()
    private val deleteTextBounds = Rect()

    private val deleteDrawable = config.deleteDrawable
    private val drawableWidth = config.deleteDrawable.intrinsicWidth
    private val drawableHeight = config.deleteDrawable.intrinsicHeight
    private val deleteTextMarginX = config.deleteTextMarginX

    init {
        deleteTextPaint.apply {
            style = Paint.Style.FILL
            color = config.textColor
            isAntiAlias = true
            textSize = config.deleteTextSize
            getTextBounds(DELETE_TEXT, 0, DELETE_TEXT.length, deleteTextBounds)
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onChildDraw(
            canvas: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        val deleteTextHeight = deleteTextBounds.bottom - deleteTextBounds.top
        val deleteTextWidth = deleteTextPaint.measureText(DELETE_TEXT)

        val deleteIconMargin = (itemHeight - drawableHeight - deleteTextHeight) / 2
        val deleteIconTop = itemView.top + deleteIconMargin
        val deleteIconBottom = deleteIconTop + drawableHeight

        val deleteTextLeft = itemView.right - deleteTextWidth - deleteTextMarginX
        val deleteTextBottom = deleteIconBottom + deleteTextHeight.toFloat()

        val deleteIconLeft = deleteTextLeft + (deleteTextWidth - drawableWidth) / 2
        val deleteIconRight = deleteIconLeft + drawableWidth

        // Drawing the background.
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(canvas)

        // Drawing "Delete" text.
        canvas.drawText(DELETE_TEXT, deleteTextLeft, deleteTextBottom, deleteTextPaint)

        // Drawing the delete icon.
        deleteDrawable.setBounds(deleteIconLeft.toInt(), deleteIconTop, deleteIconRight.toInt(), deleteIconBottom)
        deleteDrawable.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    companion object {
        private const val DELETE_TEXT = "Delete"
    }
}

internal class FilterDialogFragment : DialogFragment() {

    private lateinit var homeViewModel: HomeViewModel

    @Inject internal lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = requireNotNull(activity) { "Activity should not be null in onActivityCreated" }
        homeViewModel = viewModelProvider.getViewModel(activity)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireNotNull(context) { "Context cannot be null when creating a filter dialog!!" })

        return builder.apply {
            setTitle("Filter List")
            setCancelable(true)
            setSingleChoiceItems(options, -1, { _, which -> optionSelected(which) })
        }.create()
    }

    private fun optionSelected(which: Int) {
        val option = Options.valueOf(options[which].toUpperCase())
        when (option) {
            Options.FAVORITE -> homeViewModel.retrieveFavoriteNotes()
            Options.STARRED -> homeViewModel.retrieveStarredNotes()
            Options.NONE -> homeViewModel.retrieveNotes()
        }
        dismiss()
    }

    companion object {
        private val options = Options.values().map { it.value }.toTypedArray()
    }

    private enum class Options(val value: String) {
        FAVORITE("Favorite"), STARRED("Starred"), NONE("None")
    }
}