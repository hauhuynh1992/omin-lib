package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_voucher_invoice.view.*
import java.util.*

class VoucherInvoiceAdapter(
    activity: Activity,
    list: ArrayList<Voucher>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<Voucher> = list
    private var mActivity: Activity = activity
    private lateinit var clickListener: OnItemClickListener
    private var currentPossition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.item_voucher_invoice, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    fun resetCurrentPosition() {
        currentPossition = -1
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        ImageUtils.loadImage(
            mActivity,
            (holder as ItemViewHolder).imgThumbnail,
            mList[position].collection_image,
            ImageUtils.TYPE_CART
        )
        holder.tv_voucher_label.text = mList[position].voucher_label
        if (mList[position].condition_type_label.isNotBlank() && mList[position].condition_type != 0) {
            holder.tv_condition_type_label.visibility = View.VISIBLE
            holder.tv_condition_type_label.text =
                mList[position].condition_type_label + " " + Functions.format(mList[position].condition_value)

        }  else if (mList[position].condition_type == 1 && mList[position].max_applied_value > 0) {
            holder.tv_condition_type_label.visibility = View.VISIBLE
            holder.tv_condition_type_label.text =
                mActivity.getString(R.string.max_voucher_value) + " " + Functions.format(mList[position].max_applied_value)

        } else {
            holder.tv_condition_type_label.visibility = View.GONE
        }
        if (currentPossition == position) {
            holder.tick.visibility = View.VISIBLE
        } else {
            holder.tick.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            notifyDataSetChanged()
            currentPossition = position
            clickListener.onItemClick(position)
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            if (hasFocus) {
                holder.cv_item.cardElevation =
                    mActivity.resources.getDimension(R.dimen._4sdp)
                Functions.animateScaleUp(holder.cv_item, 1.01f)
                holder.view_green.visibility = View.VISIBLE
            } else {
                holder.cv_item.cardElevation =
                    mActivity.resources.getDimension(R.dimen._1sdp)
                Functions.animateScaleDown(holder.cv_item)
                holder.view_green.visibility = View.GONE
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cv_item: CardView = view.cv_item
        val mView: View = view
        val imgThumbnail: ImageView = view.findViewById(R.id.img_thumbnail)
        var product: Voucher? = null
        var tick: ImageView = view.findViewById(R.id.tick)
        val view_green: View = view.findViewById(R.id.view_green)
        val tv_voucher_label: SfTextView = view.findViewById(R.id.tv_voucher_label)
        val tv_condition_type_label: SfTextView = view.findViewById(R.id.tv_condition_type_label)
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }
}