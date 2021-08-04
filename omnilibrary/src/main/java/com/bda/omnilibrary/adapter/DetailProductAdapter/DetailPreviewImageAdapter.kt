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


class DetailPreviewImageAdapter(activity: BaseActivity, list: ArrayList<Product.MediaType>) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product.MediaType> = list
    private lateinit var clickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.detail_preview_image_item -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_preview_image_item, parent, false)
                v.isFocusable = true
                v.isFocusableInTouchMode = true
                ItemImageViewHolder(v)
            }
            else -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_preview_video_item, parent, false)
                v.isFocusable = true
                ItemVideoViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemImageViewHolder -> {
                ImageUtils.loadImage(
                    mActivity,
                    holder.image_preview_image,
                    mList[position].icon, ImageUtils.TYPE_PRODUCT
                )
                holder.itemView.setOnClickListener {
                    val numVideos = mList.filter { it.mediaType == "video" }.size
                    val imagePosition = position - numVideos
                    clickListener.onItemImageClick(imagePosition)
                }
                holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                    if (hasFocus) {
                        clickListener.onItemFocus(1)

                        Functions.setStrokeFocus(holder.cardview_preview_image, mActivity)
                    } else {

                        Functions.setLostFocus(holder.cardview_preview_image, mActivity)
                    }
                }


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
            }
            is ItemVideoViewHolder -> {
                ImageUtils.loadImage(
                    mActivity,
                    holder.image_preview_image,
                    mList[position].icon, ImageUtils.TYPE_VIDEO
                )
                holder.itemView.setOnClickListener {
                    clickListener.onItemVideoClick(position)
                }
                holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                    if (hasFocus) {
                        clickListener.onItemFocus(1)
                        Functions.setStrokeFocus(holder.cardview_preview_image, mActivity)

                    } else {
                        Functions.setLostFocus(holder.cardview_preview_image, mActivity)
                    }
                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardview_preview_image: MaterialCardView = view.cardview_preview_image
        var image_preview_image: ImageView = view.image_preview_image


    }

    @Suppress("PropertyName")
    inner class ItemVideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardview_preview_image: MaterialCardView = view.cardview_preview_image
        var image_preview_image: ImageView = view.image_preview_image


    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].mediaType == "video") {
            R.layout.detail_preview_video_item
        } else {
            R.layout.detail_preview_image_item
        }
    }

    interface OnItemClickListener {
        fun onItemVideoClick(position: Int)
        fun onItemImageClick(position: Int)
        fun onItemFocus(position: Int)
    }
}