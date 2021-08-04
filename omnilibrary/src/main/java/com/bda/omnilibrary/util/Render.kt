package com.bda.omnilibrary.util

import android.animation.AnimatorSet
import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

class Render(cx: Context, val end: () -> Unit) {
    private var duration: Long = 1000
    private val cx: Context = cx
    private var animatorSet: AnimatorSet? = null

    // render.setAnimation(Bounce.In(textView))
    fun setAnimation(animationSet: AnimatorSet) {
        this.animatorSet = animationSet
    }

    // render.start()
    fun start() {
        animatorSet!!.duration = duration
        animatorSet!!.interpolator = AccelerateDecelerateInterpolator()
        animatorSet!!.doOnEnd {
            end.invoke()
        }
        animatorSet!!.start()
    }

    // render.setDuration(2000)
    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setOnnAnimationEnd(){
        this.animatorSet!!.doOnStart {  }
        this.animatorSet!!.doOnStart {  }
    }
}