package com.sripad.notes.utils

import android.view.View

fun View?.makeVisible() {
    if (this != null) visibility = View.VISIBLE
}

fun View?.makeGone() {
    if (this != null) visibility = View.GONE
}