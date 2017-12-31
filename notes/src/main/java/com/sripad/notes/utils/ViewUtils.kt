package com.sripad.notes.utils

import android.view.View
import android.widget.TextView

fun View?.makeVisible() {
    if (this != null) visibility = View.VISIBLE
}

fun View?.makeGone() {
    if (this != null) visibility = View.GONE
}

fun TextView?.setTextOrGone(value: String?) {
    this ?: return
    if (value.isNullOrBlank()) {
        makeGone()
    } else {
        makeVisible()
        text = value
    }
}