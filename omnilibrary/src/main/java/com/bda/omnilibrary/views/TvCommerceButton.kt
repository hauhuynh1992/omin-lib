package com.bda.omnilibrary.views

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.FontUtils
import com.google.android.material.button.MaterialButton

class TvCommerceButton : MaterialButton {
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

    //todo button_tvcommerce

    private fun setFontType() {
        isAllCaps = false
        when (fontType) {
            FontType.LIGHT.type -> typeface = FontUtils.getFontMulishLight(context.assets)
            FontType.REGULAR.type -> typeface = FontUtils.getFontMulishRegular(context.assets)
            FontType.HEAVY.type -> typeface = FontUtils.getFontMulishHeavy(context.assets)
            FontType.BOLD.type -> typeface = FontUtils.getFontMulishBold(context.assets)
            FontType.MEDIUM.type -> typeface = FontUtils.getFontMulishMedium(context.assets)
            FontType.SEMI_BOLT.type -> typeface = FontUtils.getFontMulishSemiBolt(context.assets)
            FontType.ITALIC.type -> typeface = FontUtils.getFontMulishItalic(context.assets)
            FontType.MEDIUM_TEXT.type -> typeface =
                FontUtils.getFontMulishMediumText(context.assets)
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

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (focused) {
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.end_color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.end_color)
            }
            iconTint = ContextCompat.getColorStateList(context, R.color.white)
        } else {
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.background_product_detail)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.text_black_70)
            }
            iconTint = ContextCompat.getColorStateList(context, R.color.color_text_key)

        }
    }
}