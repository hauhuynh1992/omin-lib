package com.bda.omnilibrary.adapter.DetailProductAdapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.detail_preview_review_item.view.*
import java.util.*


class DetailPreviewReviewAdapter(activity: BaseActivity, list: ArrayList<Product.MediaType>) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product.MediaType> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.detail_preview_review_item, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder)

        val relativeParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position != 0) {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._10sdp).toInt(),
                0,
                0,
                0
            )
            holder.itemView.layoutParams = relativeParams
        } else {
            relativeParams.setMargins(
                0,
                0,
                0,
                0
            )
            holder.itemView.layoutParams = relativeParams
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }
        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                clickListener.onItemFocus(1)

                Functions.setStrokeFocus(holder.cardview_preview_review, mActivity)
            } else {
                Functions.setLostFocus(holder.cardview_preview_review, mActivity)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardview_preview_review: MaterialCardView = view.cardview_preview_review
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemFocus(position: Int)
    }
}