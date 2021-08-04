package com.bda.omnilibrary.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

class SfStrikeTextView : SfTextView {

    private val paint: Paint by lazy {
        Paint().apply {
            color = currentTextColor
            strokeWidth = resources.displayMetrics.density * 1
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun onPreDraw(): Boolean {
        paint.color = currentTextColor
        return super.onPreDraw()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(
            0f,
            (measuredHeight / 1.8).toFloat(),
            measuredWidth.toFloat(),
            (measuredHeight / 1.8).toFloat(), paint
        )
    }
}