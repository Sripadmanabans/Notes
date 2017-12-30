package com.sripad.notes.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.sripad.notes.R
import com.sripad.notes.note.NewNoteActivity
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    @Inject internal lateinit var homePresenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar.inflateMenu(R.menu.menu_home)
        val itemClickDisposables = toolbar.itemClicks()
                .doOnNext {
                    when (it.itemId) {
                        R.id.menu_add_note -> homePresenter.addNewNote()
                        else -> error("Unhandled menu item ($it) click")
                    }
                }
                .subscribe()

        val uiModelDisposable = homePresenter.uiModel()
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    when (it) {
                        is HomeUiModel.NewNote -> {
                            val newNoteIntent = Intent(this, NewNoteActivity::class.java)
                            startActivity(newNoteIntent)
                        }
                    }
                }
                .subscribe()

        disposables.addAll(itemClickDisposables, uiModelDisposable)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
