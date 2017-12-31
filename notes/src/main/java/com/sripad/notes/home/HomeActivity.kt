package com.sripad.notes.home

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.sripad.notes.R
import com.sripad.notes.note.NewNoteActivity
import com.sripad.notes.viewmodel.getViewModel
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var homeViewModel: HomeViewModel

    @Inject internal lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeViewModel = viewModelFactory.getViewModel(this)

        toolbar.inflateMenu(R.menu.menu_home)
        val itemClickDisposables = toolbar.itemClicks()
                .doOnNext {
                    when (it.itemId) {
                        R.id.menu_add_note -> startActivity(NewNoteActivity.navigationIntent(this))
                        else -> error("Unhandled menu item ($it) click")
                    }
                }
                .subscribe()

        disposables.addAll(itemClickDisposables)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
