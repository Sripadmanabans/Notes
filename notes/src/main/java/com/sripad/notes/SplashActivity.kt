package com.sripad.notes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window

/**
 * Activity that acts as the splash screen.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Hide both the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setContentView(R.layout.activity_splash)
    }
}
