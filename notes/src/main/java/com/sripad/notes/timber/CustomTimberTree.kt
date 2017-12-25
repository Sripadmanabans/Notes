package com.sripad.notes.timber

import timber.log.Timber

class CustomTimberTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val customMessage = "{${Thread.currentThread().name}} $message"
        super.log(priority, tag, customMessage, t)
    }
}