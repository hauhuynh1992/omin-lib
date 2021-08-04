package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.dialog.PriceDetail
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_order_price.view.*
import kotlinx.android.synthetic.main.item_supplier.view.*
import kotlinx.android.synthetic.main.item_supplier.view.container

class OrderPriceAdapter(
    val activity: BaseActivity,
    private var detail: PriceDetail,
    list: ArrayList<Pair<String, ArrayList<Product>>>,
    private val onGotoVoucher: () -> Unit,
    private val onClickSubOrder: (data: Pair<String, ArrayList<Product>>) -> Unit,
    private val onFocusFist: (b: Boolean) -> Unit
) : BaseAdapter(activity) {

    private var mList: ArrayList<Pair<String, ArrayList<Product>>> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_order_price -> {
                PriceItemViewHolder(
                    getLayoutInflater()
                        .inflate(R.layout.item_order_price, parent, false)
                )
            }

            R.layout.item_supplier -> {
                ItemViewHolder(
                    getLayoutInflater()
                        .inflate(R.layout.item_supplier, parent, false)
                )
            }

            else -> {
                throw IllegalArgumentException()
            }
        }

    }

    fun updateDetail(detail: PriceDetail) {
        this.detail = detail
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_order_price
            else -> R.layout.item_supplier
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setData(mList[position - 1].second)

            holder.vg_product.setOnClickListener {
                onClickSubOrder.invoke(mList[position - 1])
            }

            holder.vg_product.setOnFocusChangeListener { _, hasFocus ->
                holder.container.isSelected = hasFocus
            }
        } else if (holder is PriceItemViewHolder) {
            holder.setData(detail)

            holder.itemView.setOnClickListener {
                onGotoVoucher()
            }

            holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onFocusFist.invoke(true)
                } else {
                    onFocusFist.invoke(false)
                }

            }
        }
    }


    @Suppress("PropertyName")
    inner class PriceItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val price: SfTextView = view.price
        val ship: SfTextView = view.ship
        val total_vat: SfTextView = view.total_vat


        val rl_pay_live: RelativeLayout = view.rl_pay_live
        val pay_live: SfTextView = view.pay_live

        val ln_voucher: RelativeLayout = view.ln_voucher
        val voucherCode: SfTextView = view.voucherCode
        val voucher: SfTextView = view.voucher


        fun setData(detail: PriceDetail) {
            price.text = Functions.formatMoney(detail.price)
            ship.text = detail.shipping
            total_vat.text = Functions.formatMoney(detail.total)

            if (detail.voucherCode.isNotBlank()) {
                ln_voucher.visibility = View.VISIBLE

                voucherCode.text = "#${detail.voucherCode}"

                voucherCode.paint.shader = Functions.gradientText(
                    voucherCode,
                    ContextCompat.getColor(activity, R.color.title_orange_FF9736),
                    ContextCompat.getColor(activity, R.color.title_orange_DA5205)
                )

                voucher.text = "-${Functions.formatMoney(detail.voucher)}"
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.name
        val order_id: SfTextView = view.order_id
        val product_count: SfTextView = view.product_count
        val delivery_day: SfTextView = view.delivery_day

        val vg_product: RecyclerView = view.vg_product

        val container: LinearLayout = view.container

        fun setData(list: ArrayList<Product>) {
            name.text = list[0].supplier.supplier_name
            product_count.text =
                mActivity.getString(R.string.text_number_item, list.size.toString())

            delivery_day.text =
                mActivity.getString(R.string.text_shipping_time, Functions.shippingTime(list))

            vg_product.adapter = SupplierProductAdapter(activity, list)

        }
    }


    override fun getItemCount(): Int {
        return mList.size + 1
    }
}