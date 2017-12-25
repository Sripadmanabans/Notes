package com.sripad.notes.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import com.sripad.notes.R
import com.sripad.notes.home.HomeActivity
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Activity that acts as the splash screen.
 */
class SplashActivity : AppCompatActivity() {

    private var timerDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Hide both the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        timerDisposable = Observable.interval(SPLASH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val homeActivityIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeActivityIntent)
                }
    }

    override fun onStop() {
        super.onStop()
        timerDisposable?.dispose()
    }

    companion object {
        private const val SPLASH_TIME_INTERVAL_SECONDS = 2L
    }
}
