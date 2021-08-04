package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.addcart_item.view.*
import java.util.*


class AddCartAdapter(activity: BaseActivity, list: ArrayList<Product>) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.addcart_item, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val relativeParams =
            (holder as ItemViewHolder).layout_content_addcart.layoutParams as RecyclerView.LayoutParams
        if (position != 0) {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._minus4sdp).toInt(),
                0,
                0,
                0
            )
            holder.layout_content_addcart.layoutParams = relativeParams
        } else {
            relativeParams.setMargins(
                0,
                0,
                0,
                0
            )
            holder.layout_content_addcart.layoutParams = relativeParams
        }
        holder.tv_addcart_quantity.text =
            mList[position].order_quantity.toString()
        if (position == 2) {
            holder.more_number.visibility = View.VISIBLE
            holder.tv_addcart_quantity.visibility = View.GONE
            holder.more_number.text = "+" + (mList.size - 2)
            holder.image_addcart_item.setBackgroundColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.title_white
                )
            )
        } else {
            holder.more_number.visibility = View.GONE
            holder.tv_addcart_quantity.visibility = View.VISIBLE
            ImageUtils.loadImage(
                mActivity,
                holder.image_addcart_item,
                mList[position].imageCover,
                ImageUtils.TYPE_CART
            )
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._4sdp)
                Functions.animateScaleUp(holder.layout_product_content, 1.05f)
                holder.view_green.visibility = View.VISIBLE
            } else {
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._1sdp)
                Functions.animateScaleDown(holder.layout_product_content)
                holder.view_green.visibility = View.GONE
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: CardView = view.layout_product_content
        var view_green: View = view.view_green
        val tv_addcart_quantity: SfTextView = view.tv_addcart_quantity
        val image_addcart_item: ImageView = view.image_addcart_item
        val more_number: SfTextView = view.more_number
        var layout_content_addcart: RelativeLayout = view.layout_content_addcart

    }


    override fun getItemCount(): Int {
        return if (mList.size < 4) {
            mList.size
        } else {
            3
        }
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}