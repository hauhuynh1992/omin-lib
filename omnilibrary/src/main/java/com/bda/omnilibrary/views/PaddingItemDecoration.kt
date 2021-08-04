package com.bda.omnilibrary.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingItemDecoration(private val size: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // Apply offset only to first item
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top += size
        }
    }
}