package com.bda.omnilibrary.views.flowTextView.helpers

import android.graphics.Paint
import android.text.TextPaint

class PaintHelper {

    private var mPaintHeap: ArrayList<TextPaint> = arrayListOf()

    fun getPaintFromHeap(): TextPaint{
        return if (mPaintHeap.size > 0){
            mPaintHeap.removeAt(0)
        } else {
            TextPaint(Paint.ANTI_ALIAS_FLAG)
        }
    }

    fun setColor(color: Int){
        for (pain in mPaintHeap){
            pain.color = color
        }
    }

    fun recyclePaint(paint: TextPaint){
        mPaintHeap.add(paint)
    }
}