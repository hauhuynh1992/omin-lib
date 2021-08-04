package com.bda.omnilibrary.adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_promotion.view.*

class NotificationFromShoppingTVAdapter(
    val mActivity: Activity,
    private val orders: ArrayList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentPositionFocus: Int = 0
    private lateinit var mListener: ItemShoppingTVListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification_from_shoppingtv, parent, false)
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
        holder.bind(orders.get(position))
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
                currentPositionFocus = 0
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._4sdp)

                holder.layout_product_content.strokeWidth = 6
                holder.layout_product_content.strokeColor =
                    ContextCompat.getColor(mActivity, R.color.start_color)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_product_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.start_color)
                }
            } else {
                holder.layout_product_content.cardElevation =
                    mActivity.resources.getDimension(R.dimen._1sdp)

                holder.layout_product_content.strokeWidth = 0

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_product_content.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.text_black_70)
                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val rl_con: RelativeLayout = view.rl_con
        var layout_product_content: MaterialCardView = view.layout_product_content
        val tv_title: SfTextView = view.findViewById(R.id.tv_title)
        val tv_content: SfTextView = view.findViewById(R.id.tv_content)


        init {
            view.setOnClickListener {
                mListener.onItemFromShoppingTVClicked(absoluteAdapterPosition)
            }
        }

        //todo something
        fun bind(
            order: String
        ) {
            /*tv_uid.text = mActivity.getString(R.string.invoice) + ": " + order.uid
            if (order.name != null) {
                tv_name.text =
                    "•" + "  " + "Họ và tên:" + " " + order.name + "\n" + "•" + "  " + "Số điện thoại:" + " " + order.phone
            } else {
                tv_name.text =
                    "•" + "  " + "Họ và tên:" + " " + mActivity.getString(R.string.updating)
            }
            if (order.address != null) {
                tv_address.text = "•" + "  " + "Địa chỉ:" + " " + order.address
            } else {
                tv_address.text =
                    "•" + "  " + "Địa chỉ:" + " " + mActivity.getString(R.string.updating)
            }
            if (order.province.name != null && order.province.name.length > 0 && order.district.name != null && order.district.name.length > 0) {
                tv_province_district.visibility = View.VISIBLE
                tv_province_district.text =
                    "•" + "  " + order.district.name + ", " + order.province.name
            } else {
                tv_province_district.visibility = View.GONE
            }
            tv_price.text = Functions.formatMoney(order.totalPay)

            tv_status.text = mActivity.getString(Functions.getIdOrderStatusData(order.orderStatus))*/

        }
    }

    fun getCurrentPosistionFocus(): Int {
        return currentPositionFocus
    }

    fun setItemListener(listener: ItemShoppingTVListener) {
        mListener = listener
    }

    interface ItemShoppingTVListener {
        fun onItemFromShoppingTVClicked(position: Int)
    }
}