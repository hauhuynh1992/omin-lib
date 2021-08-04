package com.bda.omnilibrary.custome

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.FontUtils

class SearchKeyboardView : LinearLayout, View.OnFocusChangeListener {

    var searchButton: View? = null

    private val row1 = arrayOf(
        Key("a"),
        Key("b"),
        Key("c"),
        Key("d"),
        Key("e"),
        Key("f"),
        Key("g"),
        Key(
            "delete",
            R.drawable.abc_delete_selector,
            R.drawable.bg_button_seach_selector
        )
        /*Key("Xóa", 0, R.drawable.menu_border_selector)*/
    )
    private val row2 = arrayOf(
        Key("h"),
        Key("i"),
        Key("j"),
        Key("k"),
        Key("l"),
        Key("m"),
        Key("n"),
        Key("&123")
    )
    private val row3 = arrayOf(
        Key("o"),
        Key("p"),
        Key("q"),
        Key("r"),
        Key("s"),
        Key("t"),
        Key("u")
    )

    private val row4 = arrayOf(
        Key("v"),
        Key("w"),
        Key("x"),
        Key("y"),
        Key("z"),
        Key("-"),
        Key("'")
    )

    private val rowNum1 = arrayOf(
        Key("1"),
        Key("2"),
        Key("3"),
        Key("&"),
        Key("#"),
        Key("("),
        Key(")"),
        Key(
            "delete",
            R.drawable.abc_delete_selector,
            R.drawable.bg_button_seach_selector
        )

    )

    private val rowNum2 = arrayOf(
        Key("4"),
        Key("5"),
        Key("6"),
        Key("@"),
        Key("!"),
        Key("?"),
        Key(":"),
        Key("abc")
    )

    private val rowNum3 = arrayOf(
        Key("7"),
        Key("8"),
        Key("9"),
        Key("0"),
        Key("."),
        Key("_"),
        Key("\"")
    )

    private val row5 = arrayOf(
        Key(context.getString(R.string.space).toLowerCase()),
        Key(context.getString(R.string.del).toLowerCase()),
        Key(context.getString(R.string.search_hint).toLowerCase())
    )

    private val row1All = arrayOf(
        Key("a"),
        Key("b"),
        Key("c"),
        Key("d"),
        Key("e"),
        Key("f"),
        Key("g"),
        Key("h"),
        Key("i"),
        Key("."),
        Key(
            "delete",
            R.drawable.abc_delete_selector,
            R.drawable.bg_button_seach_selector
        )
        /*Key("Xóa", 0, R.drawable.menu_border_selector)*/
    )

    private val row2All = arrayOf(
        Key("j"),
        Key("k"),
        Key("l"),
        Key("m"),
        Key("n"),
        Key("o"),
        Key("p"),
        Key("q"),
        Key("r"),
        Key(","),
        Key(context.getString(R.string.del))
    )

    private val row3All = arrayOf(
        Key("s"),
        Key("t"),
        Key("u"),
        Key("v"),
        Key("w"),
        Key("x"),
        Key("y"),
        Key("z"),
        Key("0"),
        Key("/")
    )

    private val row4All = arrayOf(
        Key("1"),
        Key("2"),
        Key("3"),
        Key("4"),
        Key("5"),
        Key("6"),
        Key("7"),
        Key("8"),
        Key("9")
    )

    private val row5All = arrayOf(
        Key(context.getString(R.string.space).toLowerCase()),
        Key(context.getString(R.string.search_hint).toLowerCase())
    )


    private var mCallback: OnKeyboardCallback? = null
    private var mQuery: String? = ""
    private var isFocusLeft = false
    private var isFocusBottom = false

    constructor(context: Context?) : super(context) {

    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

    }

    fun initKeyboardAll() {
        if (!isInEditMode) {
            orientation = VERTICAL
            createAllView()
            onFocusChangeListener = this
        }
    }

    private fun createRow(vararg cells: Key): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, context.resources.getDimension(R.dimen._minus8sdp).toInt())
        @Suppress("SENSELESS_COMPARISON")
        if (cells != null && cells.isNotEmpty()) {
            for (cell in cells) {
                if (cell.name == context.getString(R.string.search_hint).toLowerCase() || cell.name == context.getString(R.string.space).toLowerCase()) {
                    layoutParams.setMargins(0, 0, 0, 0)
                    break
                } else {
                    break
                }
            }
        }

        row.layoutParams = layoutParams
        val textSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getDimension(R.dimen._4ssp).toInt()
        } else {
            resources.getDimension(R.dimen._5ssp).toInt()
        }
        val height = resources.getDimension(R.dimen._30sdp).toInt()
        val width = resources.getDimension(R.dimen._30sdp).toInt()

        @Suppress("SENSELESS_COMPARISON")
        if (cells != null && cells.isNotEmpty()) {
            for (cell in cells) {
                if (cell.resourceId > 0) {
                    val params =
                        LayoutParams(width, height)
                    val button = createKeyWithIcon(cell, params)
                    row.addView(button)
                } else {
                    val params: LayoutParams = if (cell.name.length > 1) {
                        when (cell.name) {
                            context.getString(R.string.search_hint).toLowerCase() -> {
                                cell.backgroundId = R.drawable.btn_find_selector
                                LayoutParams(resources.getDimension(R.dimen._67sdp).toInt(), height)
                            }
                            context.getString(R.string.space).toLowerCase() -> {
                                cell.backgroundId = R.drawable.btn_space_selector
                                LayoutParams(resources.getDimension(R.dimen._67sdp).toInt(), height)
                            }
                            else -> {
                                LayoutParams(width, height)
                            }
                        }

                    } else {
                        LayoutParams(width, height)
                    }
                    val tvText = createKeyWithText(cell, textSize, params)

                    if (tvText != null) {
                        row.addView(tvText)

                        if (cell.name == context.getString(R.string.search_hint).toLowerCase())
                            searchButton = tvText
                    }

                }
            }
        }
        return row
    }

    private fun createKeyWithIcon(
        @NonNull key: Key,
        params: LayoutParams
    ): View {


        val button = ImageButton(context)
        button.isFocusable = true
        button.onFocusChangeListener = this
        button.setBackgroundResource(key.backgroundId)
        button.scaleType = ImageView.ScaleType.CENTER_INSIDE
        button.isFocusableInTouchMode = true
        button.setImageResource(key.resourceId)
        button.setOnClickListener {
            detectKeyWithCustomAction(
                key.name
            )
        }
        if (key.name != "delete") {
            params.setMargins(0, 0, context.resources.getDimension(R.dimen._minus8sdp).toInt(), 0)
        }
        button.setPadding(0, 0, 0, context.resources.getDimension(R.dimen._3sdp).toInt())
        button.layoutParams = params

        return button
    }

    @SuppressLint("ResourceType", "DefaultLocale")
    private fun createKeyWithText(
        @NonNull key: Key, textSize: Int,
        params: LayoutParams
    ): View {

        val button = TextView(context)
        if (key.name != context.getString(R.string.del)) {
            button.text = key.name.toUpperCase()

        } else {
            button.text = key.name

        }

        button.textSize = textSize.toFloat()
        button.onFocusChangeListener = this
        button.typeface = FontUtils.getFontSFMedium(context.assets)

        val textColorId = ContextCompat.getColorStateList(
            context,
            R.drawable.selector_button_header
        )
        button.setTextColor(textColorId)
        button.setBackgroundResource(key.backgroundId)
        button.isFocusable = true
        button.gravity = Gravity.CENTER
        button.isFocusableInTouchMode = true
        if (key.name != context.getString(R.string.del) && key.name != "/" && key.name != "9" && key.name != context.getString(R.string.search_hint).toLowerCase() && key.name != context.getString(R.string.space).toLowerCase()) {
            params.setMargins(0, 0, context.resources.getDimension(R.dimen._minus8sdp).toInt(), 0)
        }
        button.setPadding(0, 0, 0, context.resources.getDimension(R.dimen._3sdp).toInt())
        if (key.name == context.getString(R.string.search_hint).toLowerCase() || key.name == context.getString(R.string.space).toLowerCase()) {
            button.setPadding(0, 0, 0, context.resources.getDimension(R.dimen._2sdp).toInt())
        }

        button.layoutParams = params
        button.setOnClickListener {
            detectKeyWithCustomAction(
                key.name
            )
        }

        return button
    }

    private fun detectKeyWithCustomAction(text: String) {
        var isClickSearch = false
        when (text) {
            "&123" -> {
                replaceABCToNumberView()
                return
            }
            "abc" -> {
                replaceNumberToABCView()
                return
            }
            context.getString(R.string.space).toLowerCase() -> mQuery += " "
            context.getString(R.string.del) -> mQuery = ""
            "delete" -> if (mQuery != null && mQuery!!.isNotEmpty()) mQuery =
                mQuery!!.substring(0, mQuery!!.length - 1)
            context.getString(R.string.search_hint).toLowerCase()-> {
                isClickSearch = true
            }

            else -> mQuery += text
        }
        if (mCallback != null) mCallback!!.onSearchSubmit(mQuery, isClickSearch)
    }

    fun addText(text: String) {
        mQuery = text
    }

    private fun createAllView() {
        addView(createRow(*row1All))
        addView(createRow(*row2All))
        addView(createRow(*row3All))
        addView(createRow(*row4All))
        addView(createRow(*row5All))
    }

    private fun createABCView() {

        addView(createRow(*row1))
        addView(createRow(*row2))
        addView(createRow(*row3))
        addView(createRow(*row4))
        addView(createRow(*row5))
    }

    private fun createNumberView() {
        addView(createRow(*rowNum1))
        addView(createRow(*rowNum2))
        addView(createRow(*rowNum3))
        addView(createRow(*row5))
    }

    private fun replaceABCToNumberView() {
        removeAllViews()
        createNumberView()
        requestLayout()
        val focusView = getChildAt(0)
        focusView?.requestFocus()
    }

    private fun replaceNumberToABCView() {
        removeAllViews()
        createABCView()
        requestLayout()
        val focusView = getChildAt(0)
        focusView?.requestFocus()
    }

    private var view: View? = null
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        view = v
        if (mCallback != null) {
            mCallback!!.onSearchFocusable(hasFocus)
        }

        if (hasFocus) {
            if (view is TextView) {

                if ((view as TextView).text == "A" || (view as TextView).text == "J"
                    || (view as TextView).text == "S" || (view as TextView).text == "1"
                    || (view as TextView).text == context.getString(R.string.space).toUpperCase()
                ) {
                    isFocusLeft = true
                }
            }

            if (view is TextView) {
                if ((view as TextView).text == context.getString(R.string.search_hint).toUpperCase()
                    || (view as TextView).text == context.getString(R.string.space).toUpperCase()
                ) {
                    isFocusBottom = true
                }
            }
        } else {
            if (view is TextView) {
                if ((view as TextView).text == "A" || (view as TextView).text == "J"
                    || (view as TextView).text == "S" || (view as TextView).text == "1"
                    || (view as TextView).text == context.getString(R.string.space).toUpperCase()
                ) {
                    isFocusLeft = false
                }
            }

            if (view is TextView) {
                if ((view as TextView).text == context.getString(R.string.search_hint).toUpperCase()
                    || (view as TextView).text == context.getString(R.string.space).toUpperCase()
                ) {
                    isFocusBottom = false
                }
            }
        }
    }

    private class Key @JvmOverloads constructor(
        var name: String, @field:DrawableRes @DrawableRes var resourceId: Int = 0,
        @field:DrawableRes var backgroundId: Int = R.drawable.bg_button_seach_selector
    )

    fun setOnKeyboardCallback(callback: OnKeyboardCallback?) {
        mCallback = callback
    }

    interface OnKeyboardCallback {
        fun onSearchSubmit(query: String?, isClickSearch: Boolean)
        fun onSearchFocusable(isFocus: Boolean)
        fun onDpadDownInBottom()
        fun onDpadLeftInLeft()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (isFocusBottom) {
                        if (mCallback != null) {
                            mCallback!!.onDpadDownInBottom()
                        }
                    }
                }

                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (isFocusLeft) {
                        if (mCallback != null) {
                            mCallback!!.onDpadLeftInLeft()
                        }
                    }
                }

            }
        }
        return super.dispatchKeyEvent(event)
    }
}