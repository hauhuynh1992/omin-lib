package com.bda.omnilibrary.adapter.collection

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_collection_name.view.*

class CollectionTempHeaderNameAdapter(
    activity: BaseActivity,
    list: ArrayList<String>
) : BaseAdapter(activity) {
    private var mList: ArrayList<String> = list
    private lateinit var clickListener: OnItemClickListener

    var currentSelectedPosition = 0
    var isFocusName = true
    var isCallThisCallBack = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_collection_name, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).text_contain.text = mList[position]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            holder.item_content.outlineSpotShadowColor =
                ContextCompat.getColor(mActivity, R.color.trans)
            holder.item_content.outlineAmbientShadowColor =
                ContextCompat.getColor(mActivity, R.color.trans)
        }

        if (currentSelectedPosition == position) {
            holder.text_contain.setTextColor(ContextCompat.getColor(mActivity, R.color.end_color))
        } else {
            holder.text_contain.setTextColor(
                ContextCompat.getColorStateList(
                    mActivity,
                    R.drawable.selector_collection_name
                )
            )
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            holder.text_contain.isSelected = hasFocus
            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.color_shadow)
                }

                if (isFocusName && isCallThisCallBack) {
                    clickListener.onItemFocus(position)
                }

                if (position == currentSelectedPosition)
                    holder.text_contain.setTextColor(
                        ContextCompat.getColorStateList(
                            mActivity,
                            R.drawable.selector_collection_name
                        )
                    )

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.trans)
                }

                if (position == currentSelectedPosition)
                    holder.text_contain.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.end_color
                        )
                    )
            }
        }

        holder.itemView.setOnClickListener {
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text_contain: SfTextView = view.text_contain
        val item_content: MaterialCardView = view.item_content
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemFocus(index: Int)
    }
}