package com.bda.omnilibrary.adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_promotion.view.*

class NotificationUpdateOrderAdapter(
    val mActivity: Activity,
    private val orders: ArrayList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentPositionFocus: Int = 0
    private lateinit var mListener: UpdateOrderListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_update_order, parent, false)
        view.isFocusableInTouchMode = true
        view.isFocusable = true
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.bind(orders[position])
        val relativeParams =
            holder.rl_con.layoutParams as RecyclerView.LayoutParams
        if (position == 0) {
            relativeParams.setMargins(
                0,
                0,
                0,
                0
            )
        } else {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._1sdp).toInt(),
                0,
                0,
                0
            )
        }
        holder.rl_con.layoutParams = relativeParams

        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            if (hasFocus) {
                currentPositionFocus = position
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._4sdp)

                holder.layout_product_content.strokeWidth = 6
                holder.layout_product_content.strokeColor =
                    ContextCompat.getColor(mActivity, R.color.start_color)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_product_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.start_color)
                }

                val textColorId = ContextCompat.getColorStateList(
                    mActivity,
                    R.color.start_color
                )
                val icRight = ContextCompat.getDrawable(
                    mActivity,
                    R.drawable.ic_right_blue
                )

                holder.tv_view_detail.setTextColor(textColorId)
                holder.ic_right.setImageDrawable(icRight)

            } else {
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._1sdp)

                holder.layout_product_content.strokeWidth = 0

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_product_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.text_black_70)
                }
                val icRight = ContextCompat.getDrawable(
                    mActivity,
                    R.drawable.ic_right_black
                )
                val textColorId = ContextCompat.getColorStateList(
                    mActivity,
                    R.color.title_black
                )
                holder.tv_view_detail.setTextColor(textColorId)
                holder.ic_right.setImageDrawable(icRight)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val rl_con: RelativeLayout = view.rl_con
        var layout_product_content: MaterialCardView = view.layout_product_content

        /*val tv_order_id: SfTextView = view.findViewById(R.id.tv_order_id)
        val tv_order_date: SfTextView = view.findViewById(R.id.tv_order_id)*/
        val tv_content: SfTextView = view.findViewById(R.id.tv_content)
        val tv_status: SfTextView = view.findViewById(R.id.tv_status)
        val img_product: ImageView = view.findViewById(R.id.img_product)
        val ic_right: ImageView = view.findViewById(R.id.ic_right)
        val tv_view_detail: TextView = view.findViewById(R.id.tv_view_detail)

        init {


            view.setOnClickListener {
                mListener.onReviewOrderClicked(absoluteAdapterPosition)
            }
        }

        //todo something
        fun bind(
            order: String
        ) {
//            tv_uid.text = mActivity.getString(R.string.invoice) + ": " + order.uid
//            if (order.name != null) {
//                tv_name.text =
//                    "•" + "  " + "Họ và tên:" + " " + order.name + "\n" + "•" + "  " + "Số điện thoại:" + " " + order.phone
//            } else {
//                tv_name.text =
//                    "•" + "  " + "Họ và tên:" + " " + mActivity.getString(R.string.updating)
//            }
//            if (order.address != null) {
//                tv_address.text = "•" + "  " + "Địa chỉ:" + " " + order.address
//            } else {
//                tv_address.text =
//                    "•" + "  " + "Địa chỉ:" + " " + mActivity.getString(R.string.updating)
//            }
//            if (order.province.name != null && order.province.name.length > 0 && order.district.name != null && order.district.name.length > 0) {
//                tv_province_district.visibility = View.VISIBLE
//                tv_province_district.text =
//                    "•" + "  " + order.district.name + ", " + order.province.name
//            } else {
//                tv_province_district.visibility = View.GONE
//            }
//            tv_price.text = Functions.formatMoney(order.totalPay)
//
//            tv_status.text = mActivity.getString(Functions.getIdOrderStatusData(order.orderStatus))

        }
    }

    fun getCurrentPositionFocus(): Int {
        return currentPositionFocus
    }

    fun setUpdateOrderListener(listener: UpdateOrderListener) {
        mListener = listener
    }

    interface UpdateOrderListener {
        fun onReviewOrderClicked(position: Int)
        fun onConfirmOrderClicked(position: Int)
    }
}