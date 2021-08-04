package com.bda.omnilibrary.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.FontUtils

class SfButton : Button {
    // default is light
    private var fontType: Int = 1

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SfView, defStyle, 0
        )
        fontType = a.getInt(R.styleable.SfView_type, 1)
        a.recycle()

        setFontType()
    }

    private fun setFontType() {
        isAllCaps = false
        when (fontType) {
            FontType.LIGHT.type -> typeface = FontUtils.getFontLatoLight(context.assets)
            FontType.REGULAR.type -> typeface = FontUtils.getFontLatoRegular(context.assets)
            FontType.HEAVY.type -> typeface = FontUtils.getFontLatoHeavy(context.assets)
            FontType.BOLD.type -> typeface = FontUtils.getFontLatoBold(context.assets)
            FontType.MEDIUM.type -> typeface = FontUtils.getFontLatoMedium(context.assets)
            FontType.SEMI_BOLT.type -> typeface = FontUtils.getFontLatoSemiBolt(context.assets)
            FontType.ITALIC.type -> typeface = FontUtils.getFontLatoItalic(context.assets)
            FontType.MEDIUM_TEXT.type -> typeface = FontUtils.getFontLatoMediumText(context.assets)
        }
    }

    private enum class FontType(val type: Int) {
        LIGHT(1),
        REGULAR(2),
        HEAVY(3),
        BOLD(4),
        MEDIUM(5),
        SEMI_BOLT(6),
        ITALIC(7),
        MEDIUM_TEXT(8)
    }
}