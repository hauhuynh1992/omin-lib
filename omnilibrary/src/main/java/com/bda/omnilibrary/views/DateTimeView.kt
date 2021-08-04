package com.bda.omnilibrary.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.TickleManager

class DateTimeView : AppCompatTextView, TickleManager.TickleListener {
    private var mTickleManager: TickleManager? = null
    private var mIsDateEnabled = true

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mTickleManager = TickleManager.instance()
        updateListener()
    }

    private fun updateListener() {
        mTickleManager?.removeListener(this)
        if (visibility == View.VISIBLE) {
            mTickleManager?.addListener(this)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        updateListener()
    }

    override fun onTickle() {
        if (visibility == View.VISIBLE) {
            text = if (mIsDateEnabled) {
                Functions.getCurrentDateTimeShort(context)
            } else {
                Functions.getCurrentTimeShort(context)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Player has been closed
        mTickleManager?.removeListener(this)
    }

    fun showDate(show: Boolean) {
        mIsDateEnabled = show
    }
}
