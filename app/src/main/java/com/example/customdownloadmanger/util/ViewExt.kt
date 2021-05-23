package com.example.customdownloadmanger.util

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showUnless(isVisible: Boolean) {
    if (isVisible) {
        show()
    } else {
        hide()
    }
}

fun View.invisible() {
    visibility = View.INVISIBLE
}