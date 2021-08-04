package com.bda.omnilibrary.adapter.DetailProductAdapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.detail_preview_image_item.view.*
import java.util.*


class DetailPreviewVideoAdapter(activity: BaseActivity, list: ArrayList<Product.MediaType>) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product.MediaType> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.detail_preview_video_item, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        ImageUtils.loadImage(
            mActivity,
            (holder as ItemViewHolder).image_preview_image,
            mList[position].icon, ImageUtils.TYPE_VIDEO
        )

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }
        holder.itemView.setOnFocusChangeListener { v: View, hasFocus: Boolean ->
            if (hasFocus) {
                clickListener.onItemFocus(2)
                holder.cardview_preview_image.cardElevation =
                    mActivity.resources.getDimension(R.dimen._4sdp)
                Functions.animateScaleUp(v, 1.05f)
            } else {
                holder.cardview_preview_image.cardElevation =
                    mActivity.resources.getDimension(R.dimen._1sdp)
                Functions.animateScaleDown(v)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardview_preview_image: MaterialCardView = view.cardview_preview_image
        var image_preview_image: ImageView = view.image_preview_image
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