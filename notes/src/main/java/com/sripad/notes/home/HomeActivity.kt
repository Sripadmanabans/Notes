package com.sripad.notes.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sripad.notes.R
import dagger.android.AndroidInjection

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}
