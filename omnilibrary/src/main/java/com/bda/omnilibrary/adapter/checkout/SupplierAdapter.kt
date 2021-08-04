package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier.view.*

class SupplierAdapter(
    val activity: BaseActivity,
    list: ArrayList<Pair<String, ArrayList<Product>>>,
    private val isHighlightDeliveryDay: Boolean = false,
    private val onClick: (data: Pair<String, ArrayList<Product>>) -> Unit,
) : BaseAdapter(activity) {

    private var mList: ArrayList<Pair<String, ArrayList<Product>>> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setData(mList[position].second, isHighlightDeliveryDay)

            holder.vg_product.setOnClickListener {
                onClick.invoke(mList[position])
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

        fun setData(list: ArrayList<Product>, isHighlightDeliveryDay: Boolean) {
            name.text = list[0].supplier.supplier_name
            product_count.text =
                mActivity.getString(R.string.text_number_item, list.size.toString())

            val time = Functions.shippingTime(list)
            if (time.isNotBlank())
                delivery_day.text =
                    mActivity.getString(R.string.text_shipping_time, Functions.shippingTime(list))
            else
                delivery_day.text = ""

            if (isHighlightDeliveryDay) {
                product_count.setTextColor(ContextCompat.getColor(mActivity, R.color.color_41AE96))
                delivery_day.setTextColor(ContextCompat.getColor(mActivity, R.color.color_41AE96))
            }

            vg_product.setOnFocusChangeListener { _, hasFocus ->
                container.isSelected = hasFocus

            }

            vg_product.adapter = SupplierProductAdapter(activity, list)

        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}