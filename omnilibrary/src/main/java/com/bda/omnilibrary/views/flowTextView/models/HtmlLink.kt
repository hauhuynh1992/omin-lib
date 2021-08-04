package com.bda.omnilibrary.views.flowTextView.models

import android.text.TextPaint

class HtmlLink(
    content: String, start: Int, end: Int, xOffset: Float,
    paint: TextPaint, url: String,
) : HtmlObject(content, start, end, xOffset, paint) {

    var width: Float = 0f
    var height: Float = 0f
    var yOffset: Float = 0f
    var url: String = url
}