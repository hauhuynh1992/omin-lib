package com.bda.omnilibrary.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.bda.omnilibrary.R

class ColorPickerView : RelativeLayout, RadioCheckable {
    private lateinit var mColorView: View
    private lateinit var mStrokeView: View

    private var mColor: Int? = null

    private var mStokeColor: Int? = null

    // Variables
    private lateinit var mInitialBackgroundDrawable: Drawable
    private var mOnClickListener: OnClickListener? = null
    private var mOnTouchListener: OnTouchListener? = null
    private var mOnKeyListener: OnKeyListener? = null
    private var mChecked: Boolean = false
    private var mOnCheckedChangeListeners = mutableListOf<RadioCheckable.OnCheckedChangeListener>()

    // Constants
    /*companion object {
        val DEFAULT_COLOR = Color.TRANSPARENT
    }*/

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseAttributes(attrs)
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        parseAttributes(attrs)
        setupView()
    }

    private fun setupView() {
        inflateView()
        bindView()
        setCustomTouchListener()
    }

    fun getColor(): Int? {
        if (mColor != null) {
            return mColor
        }
        return null
    }

    fun setColor(color: String) {
        mColor = Color.parseColor(color)
        bindView()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ColorPickerView, 0, 0
        )

        try {
            mColor = a.getColor(
                R.styleable.ColorPickerView_color,
                Color.RED
            )
            mStokeColor =
                a.getColor(R.styleable.ColorPickerView_strokeColor, Color.GRAY)
        } finally {
            a.recycle()
        }
    }

    private fun inflateView() {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_color_selector, this, true)

        mColorView = findViewById(R.id.color)
        mStrokeView = findViewById(R.id.border)

        //mInitialBackgroundDrawable = background
    }

    protected fun bindView() {
        if (mColor != null) {
            val unselected = GradientDrawable()
            unselected.shape = GradientDrawable.OVAL
            unselected.setColor(mColor!!)
            unselected.setSize(resources.getDimensionPixelSize(R.dimen._18sdp), resources.getDimensionPixelSize(R.dimen._18sdp))
            mColorView.background = unselected

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(Color.TRANSPARENT)
            shape.setSize(resources.getDimensionPixelSize(R.dimen._24sdp), resources.getDimensionPixelSize(R.dimen._24sdp))
            shape.setStroke(1, mColor!!)
            mStrokeView.background = shape

        }
    }

    fun setCustomTouchListener() {
        //super.setOnTouchListener(KeyEvenListener())

        super.setOnKeyListener(KeyEvenListener())

    }

    override fun setOnClickListener(l: OnClickListener?) {
        //super.setOnClickListener(l)
        if (l != null) {
            mOnClickListener = l
        }
    }


    override fun setOnTouchListener(onTouchListener: OnTouchListener?) {
        mOnTouchListener = onTouchListener
    }

    override fun setOnKeyListener(l: OnKeyListener?) {
        //super.setOnKeyListener(l)
        mOnKeyListener = l
    }

    private fun onTouchDown(motionEvent: MotionEvent) {
        isChecked = true
    }

    private fun onTouchUp(motionEvent: MotionEvent) {
        // Handle user defined click listeners
        if (mOnClickListener != null) {
            mOnClickListener!!.onClick(this)
        }
    }

    private fun onTouchDown(motionEvent: KeyEvent) {
        isChecked = true
    }

    private fun onTouchUp(motionEvent: KeyEvent) {
        // Handle user defined click listeners
        if (mOnClickListener != null) {
            mOnClickListener!!.onClick(this)
        }
    }


    override fun addOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        mOnCheckedChangeListeners.add(onCheckedChangeListener)
    }

    override fun removeOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        mOnCheckedChangeListeners.remove(onCheckedChangeListener)
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            if (!mOnCheckedChangeListeners.isEmpty()) {
                for (i in 0 until mOnCheckedChangeListeners.size) {
                    mOnCheckedChangeListeners[i].onCheckedChanged(this, mChecked)
                }
            }
            if (mChecked!!) {
                setCheckedState()
            } else {
                setNormalState()
            }
        }
    }

    fun setCheckedState() {
        mStrokeView.visibility = View.VISIBLE
    }

    fun setNormalState() {
        mStrokeView.visibility = View.INVISIBLE
    }

    private inner class KeyEvenListener : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event != null) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> onTouchDown(event)
                        MotionEvent.ACTION_UP -> onTouchUp(event)
                    }
                } else {
                    return false
                }
            }
            if (mOnKeyListener != null) {
                mOnKeyListener!!.onKey(v, keyCode, event)
            }
            return true
        }
    }
}