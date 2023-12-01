package com.example.healthcare.Animation
import android.app.Activity
import android.content.Context
import com.blogspot.atifsoftwares.animatoolib.*;
object Transition {
    fun animateZoomIn(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.animate_zoom_exit,
            R.anim.animate_zoom_enter
        )
    }
}