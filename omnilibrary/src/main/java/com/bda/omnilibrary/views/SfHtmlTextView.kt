package com.bda.omnilibrary.views

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.AttributeSet

class SfHtmlTextView : SfTextView {

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

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT), type)
        } else {
            super.setText(Html.fromHtml(text.toString()), type)
        }
    }
}