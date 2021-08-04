package com.bda.omnilibrary.ui.liveStreamActivity

import android.view.View
import androidx.fragment.app.Fragment

abstract class LiveStreamBaseFragment : Fragment() {
    private var lastFocusId: Int? = null
    var lastFocus: View? = null

    fun setLastFocusId(id: Int?) {
        this.lastFocusId = id
    }

    fun lastFocusId() = lastFocusId

    abstract fun requestFocus()
    abstract fun isAbleToHideByClickLeft(): Boolean
}