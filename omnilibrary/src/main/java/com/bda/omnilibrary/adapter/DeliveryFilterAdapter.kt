package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_promotion.view.*

class DeliveryFilterAdapter(
    activity: BaseActivity,
    private val orders: ArrayList<ListOrderResponceV3.Data>
) :
    BaseAdapter(activity) {
    private lateinit var clickListener: OnItemClickListener
    private var currentPositionFocus: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == R.layout.item_order_list_empt) {
            v = getLayoutInflater()
                .inflate(R.layout.item_order_list_empt, parent, false)
            EmptyViewHolder(v)
        } else {
            v = getLayoutInflater()
                .inflate(R.layout.item_delivery_filter, parent, false)
            ItemViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return if (orders.size > 0) {
            orders.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.size == 0) {
            R.layout.item_order_list_empt
        } else {
            R.layout.item_delivery_filter
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        currentPositionFocus = position
                        holder.itemView.requestFocus()
                        holder.itemView.invalidate()
                        android.os.Handler().postDelayed({
                            holder.bn_shopping.requestFocus()
                        }, 0)
                    }
                }

                holder.rl_bn_continue_shopping.setOnFocusChangeListener { _, isFocus ->
                    holder.bn_shopping.isSelected = isFocus
                }

                holder.rl_bn_continue_shopping.setOnClickListener {
                    clickListener.onShoppingContinueClick()
                }
            }
            is ItemViewHolder -> {
                holder.bind(orders[position], position)

                holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
                    if (hasFocus) {
                        currentPositionFocus = position
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
        }
    }

    @Suppress("PropertyName")
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_shopping: SfTextView = view.findViewById(R.id.bn_continue_shopping)
        val rl_bn_continue_shopping: RelativeLayout =
            view.findViewById(R.id.rl_bn_continue_shopping)
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val rl_con: RelativeLayout = view.rl_con

        val tv_view_detail: TextView = view.findViewById(R.id.tv_view_detail)
        val ic_right: ImageView = view.findViewById(R.id.ic_right)
        val img_product: ImageView = view.findViewById(R.id.img_product)
        val tv_product_name: SfTextView = view.findViewById(R.id.tv_product_name)
        val tv_more_product: SfTextView = view.findViewById(R.id.tv_more_product)
        val bg_status: RelativeLayout = view.findViewById(R.id.bg_status)
        val tv_status: TextView = view.findViewById(R.id.tv_status)
        val tv_order_id: SfTextView = view.findViewById(R.id.tv_order_id)
        val tv_order_date: SfTextView = view.findViewById(R.id.tv_order_date)
        val tv_delivery_date: SfTextView = view.findViewById(R.id.tv_delivery_date)
        val tv_total_money: SfTextView = view.findViewById(R.id.tv_total_money)

        init {

        }

        //todo something
        @Suppress("UNNECESSARY_SAFE_CALL")
        fun bind(
            order: ListOrderResponceV3.Data, position: Int
        ) {
            itemView.setOnClickListener {
                clickListener.onItemClick(order, position)
            }
            order.uid?.let {
                tv_order_id.text = it
            }

            order.created_at?.let {
                tv_order_date.text = it
            }

            order.created_at?.let {
                tv_delivery_date.text = it
            }

            order.created_at?.let {
                tv_order_date.text = it
            }

            order.totalPay?.let {
                tv_total_money.text = Functions.formatMoney(it)
            }

            order.sub_orders?.let { list_products ->
                tv_product_name.text = list_products[0].items[0].displayNameDetail
                ImageUtils.loadImage(
                    mActivity,
                    img_product,
                    list_products[0].items[0].thumb,
                    ImageUtils.TYPE_CART
                )
                if (list_products.size > 1) {
                    tv_more_product.visibility = View.VISIBLE
                    tv_more_product.text =
                        mActivity.getString(R.string.text_more_products, list_products.size - 1)
                } else {
                    tv_more_product.visibility = View.GONE
                }
            }
            when (order.orderStatus) {
                ORDER_NEW_CREATE,
                ORDER_CONFIRMING,
                ORDER_CONFRIMED,
                ORDER_REQUEST_CANCEL -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_processing
                    )
                    bg_status.background = status
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_processing
                    )
                    tv_status.text = mActivity.getString(R.string.txt_order_processing)
                    tv_status.setTextColor(textColorId)
                }
                ORDER_DELIVERING -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_delivering
                    )
                    bg_status.background = status
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_delivering
                    )
                    tv_status.text = mActivity.getString(R.string.txt_order_delivering)
                    tv_status.setTextColor(textColorId)
                }
                ORDER_DELIVERED -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_delivered
                    )
                    bg_status.background = status
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_delivered
                    )
                    tv_status.text = mActivity.getString(R.string.txt_order_delivered)
                    tv_status.setTextColor(textColorId)
                }
                ORDER_CANCEL -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_cancelled
                    )
                    bg_status.background = status
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_cancelled
                    )
                    tv_status.text = mActivity.getString(R.string.txt_order_cancelled)
                    tv_status.setTextColor(textColorId)
                }
                ORDER_REFUNDING_ORDER,
                ORDER_REFUNDED_ORDER, -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_returning
                    )
                    bg_status.background = status
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_returning
                    )
                    tv_status.text = mActivity.getString(R.string.txt_order_returning)
                    tv_status.setTextColor(textColorId)
                }
                ORDER_REFUNDED -> {
                    val status = ContextCompat.getDrawable(
                        mActivity,
                        R.mipmap.bg_refund
                    )
                    val textColorId = ContextCompat.getColorStateList(
                        mActivity,
                        R.color.color_order_refund
                    )
                    bg_status.background = status
                    tv_status.text = mActivity.getString(R.string.txt_order_refunded)
                    tv_status.setTextColor(textColorId)
                }
            }
        }
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    fun setData(mData: ArrayList<ListOrderResponceV3.Data>) {
        orders.clear()
        orders.addAll(mData)
        notifyDataSetChanged()
    }

    fun deleteOrderById(orderId: String) {
        orders.indexOfFirst {
            it.uid == orderId
        }.let { mPosistion ->
            if (mPosistion != -1) {
                orders.removeAt(mPosistion)
                notifyItemRemoved(mPosistion)
                notifyItemRangeChanged(mPosistion, orders.size)
            }
        }
        notifyDataSetChanged()
    }

    fun addAll(mData: ArrayList<ListOrderResponceV3.Data>) {
        orders.addAll(mData)
        notifyDataSetChanged()
    }

    fun clearAll() {
        orders.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(data: ListOrderResponceV3.Data, position: Int)
        fun onShoppingContinueClick()

    }

    fun getCurrentPositionFocus(): Int {
        return currentPositionFocus
    }

    companion object {
        const val ORDER_NEW_CREATE = 0
        const val ORDER_CONFIRMING = 1
        const val ORDER_CONFRIMED = 2
        const val ORDER_DELIVERING = 3
        const val ORDER_DELIVERED = 4
        const val ORDER_REQUEST_CANCEL = 5
        const val ORDER_CANCEL = 6
        const val ORDER_REFUNDING_ORDER = 7
        const val ORDER_REFUNDED_ORDER = 8
        const val ORDER_REFUNDED = 9
    }
}