package com.bda.omnilibrary.views.flowTextView.models

import android.text.TextPaint

open class HtmlObject(
    var content: String, var start: Int, var end: Int, var xOffset: Float,
    var paint: TextPaint?, var recycle: Boolean = false,
)