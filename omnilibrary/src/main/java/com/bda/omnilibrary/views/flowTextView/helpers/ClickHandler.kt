package com.bda.omnilibrary.views.flowTextView.helpers

import android.view.MotionEvent
import android.view.View
import com.bda.omnilibrary.views.flowTextView.OnLinkClickListener
import kotlin.math.pow
import kotlin.math.sqrt


class ClickHandler(private val mSpanParser: SpanParser) : View.OnTouchListener {

    private var mOnLinkClickListener: OnLinkClickListener? = null

    private var distance = 0.0
    private var x1: Float = 0f
    private var y1: Float = 0f
    private var x2: Float = 0f
    private var y2: Float = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val event_code = event!!.action

        if (event_code == MotionEvent.ACTION_DOWN) {
            distance = 0.0
            x1 = event.x
            y1 = event.y
        }

        if (event_code == MotionEvent.ACTION_MOVE) {
            x2 = event.x
            y2 = event.y
            distance = getPointDistance(x1, y1, x2, y2)
        }

        return if (distance < 10) { // my random guess at an acceptable drift distance to regard this as a click
            if (event_code == MotionEvent.ACTION_UP) {
                // if the event is an "up" and we havn't moved far since the "down", then it's a click
                onClick(event.x, event.y) // process the click and say whether we consumed it
            } else true
        } else false
    }

    private fun onClick(x: Float, y: Float): Boolean {
        val links = mSpanParser.getLinks()
        for (link in links) {
            val tlX = link!!.xOffset
            val tlY = link.yOffset
            val brX = link.xOffset + link.width
            val brY = link.yOffset + link.height
            if (x > tlX && x < brX) {
                if (y > tlY && y < brY) {
                    // collision
                    onLinkClick(link.url)
                    return true // the click was consumed
                }
            }
        }
        return false
    }

    private fun onLinkClick(url: String) {
        mOnLinkClickListener?.onLinkClick(url)
    }

    private fun getPointDistance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        return sqrt((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0))
    }

    fun getOnLinkClickListener(): OnLinkClickListener? {
        return mOnLinkClickListener
    }

    fun setOnLinkClickListener(mOnLinkClickListener: OnLinkClickListener?) {
        this.mOnLinkClickListener = mOnLinkClickListener
    }
}
