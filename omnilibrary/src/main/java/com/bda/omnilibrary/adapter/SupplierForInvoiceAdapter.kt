package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier.view.*

class SupplierForInvoiceAdapter(
    private val activity: BaseActivity,
    private val order: ListOrderResponceV3.Data,
) : BaseAdapter(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setData(order.sub_orders[position], order)

            holder.vg_product.setOnClickListener {

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

        fun setData(subOrder: ListOrderResponceV3.Data.SubOrder, order: ListOrderResponceV3.Data) {
            name.text = subOrder.partner?.partner_name
            order_id.text = subOrder.order_id
            order_id.visibility = View.VISIBLE

            vg_product.setOnFocusChangeListener { _, hasFocus ->
                container.isSelected = hasFocus
            }

            product_count.text =
                mActivity.getString(R.string.text_number_item, subOrder.items.size.toString())

            delivery_day.text =
                mActivity.getString(
                    R.string.text_shipping_time,
                    Functions.shippingTime(order.created_at, subOrder.partner?.shipping_time ?: 0)
                )

            vg_product.adapter = SupplierProductForInvoiceAdapter(activity, subOrder.items)

        }
    }


    override fun getItemCount(): Int {
        return order.sub_orders.size
    }
}