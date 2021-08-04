package com.bda.omnilibrary.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.Functions

class TvRadioGroup : LinearLayout {

    // Attribute Variables
    private var mCheckedId = View.NO_ID
    private var mProtectFromCheckedChange = false

    // Variables
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private val mChildViewsMap = HashMap<Int, View>()
    private var mPassThroughListener: PassThroughHierarchyChangeListener? = null
    private var mChildOnCheckedChangeListener: RadioCheckable.OnCheckedChangeListener? = null
    // Contructor

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

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttributes(attrs)
        setupView()
    }

    // Init & inflate methods

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.TvRadioGroup, 0, 0
        )
        try {
            mCheckedId =
                a.getResourceId(R.styleable.TvRadioGroup_tvRadioCheckedId, View.NO_ID)

        } finally {
            a.recycle()
        }
    }

    // Template method
    private fun setupView() {
        mChildOnCheckedChangeListener = CheckedStateTracker()
        mPassThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(mPassThroughListener)
    }

    // Overriding default behavior

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is RadioCheckable) {
            val button = child as RadioCheckable
            if (button.isChecked) {
                mProtectFromCheckedChange = true
                if (mCheckedId != View.NO_ID) {
                    setCheckedStateForView(mCheckedId, false)
                }
                mProtectFromCheckedChange = false
                setCheckedId(child.id, true)
            }
        }
        super.addView(child, index, params)
    }

    override fun setOnHierarchyChangeListener(listener: ViewGroup.OnHierarchyChangeListener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener!!.mOnHierarchyChangeListener = listener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // checks the appropriate radio button as requested in the XML file
        if (mCheckedId != View.NO_ID) {
            mProtectFromCheckedChange = true
            setCheckedStateForView(mCheckedId, true)
            mProtectFromCheckedChange = false
            setCheckedId(mCheckedId, true)
        }
    }

    private fun setCheckedId(@IdRes id: Int, isChecked: Boolean) {
        mCheckedId = id
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(
                this,
                mChildViewsMap[id]!!,
                isChecked,
                mCheckedId
            )
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LinearLayout.LayoutParams
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        var checkedView: View?
        checkedView = mChildViewsMap.get(viewId)
        if (checkedView == null) {
            checkedView = findViewById<View>(viewId)
            if (checkedView != null) {
                mChildViewsMap.put(viewId, checkedView)
            }
        }
        if (checkedView != null && checkedView is RadioCheckable) {
            (checkedView as RadioCheckable).isChecked = checked
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LinearLayout.LayoutParams(context, attrs)
    }

    //================================================================================
    // Public methods
    //================================================================================

    fun clearCheck() {
        check(View.NO_ID)
    }

    fun check(@IdRes id: Int) {
        // don't even bother
        if (id != View.NO_ID && id == mCheckedId) {
            return
        }

        if (mCheckedId != View.NO_ID) {
            setCheckedStateForView(mCheckedId, false)
        }

        if (id != View.NO_ID) {
            setCheckedStateForView(id, true)
        }

        setCheckedId(id, true)
    }

    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener
    }

    fun getOnCheckedChangeListener(): OnCheckedChangeListener? {
        return mOnCheckedChangeListener
    }

    fun setEnable(enable: Boolean) {
        for (i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            child.isEnabled = enable
        }
    }

    //================================================================================
    // Nested classes
    //================================================================================
    interface OnCheckedChangeListener {
        fun onCheckedChanged(
            radioGroup: View,
            radioButton: View,
            isChecked: Boolean,
            checkedId: Int
        )
    }

    //================================================================================
    // Inner classes
    //================================================================================
    private inner class CheckedStateTracker : RadioCheckable.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: View, isChecked: Boolean) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return
            }

            mProtectFromCheckedChange = true
            if (mCheckedId != View.NO_ID) {
                setCheckedStateForView(mCheckedId, false)
            }
            mProtectFromCheckedChange = false

            val id = buttonView.getId()
            setCheckedId(id, true)
        }
    }

    private inner class PassThroughHierarchyChangeListener : ViewGroup.OnHierarchyChangeListener {

        internal var mOnHierarchyChangeListener: ViewGroup.OnHierarchyChangeListener? = null

        /**
         * {@inheritDoc}
         */
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@TvRadioGroup && child is RadioCheckable) {
                var id = child.id
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = Functions.generateViewId()
                    child.id = id
                }
                (child as RadioCheckable).addOnCheckChangeListener(
                    mChildOnCheckedChangeListener!!
                )
                mChildViewsMap[id] = child
            }

            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@TvRadioGroup && child is RadioCheckable) {
                (child as RadioCheckable).removeOnCheckChangeListener(mChildOnCheckedChangeListener!!)
            }
            mChildViewsMap.remove(child.id)
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }
}