package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier_product.view.*

class SupplierProductForInvoiceAdapter(
    activity: BaseActivity,
    list: ArrayList<ListOrderResponceV3.Data.Item>,
) : BaseAdapter(activity) {

    private var mList: ArrayList<ListOrderResponceV3.Data.Item> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier_product, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setItem(item = mList[position], isHideDivider = position == 0)
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: LinearLayout = view.content
        val img_thumbnail: ImageView = view.img_thumbnail
        val name: SfTextView = view.name
        val divider: View = view.divider
        val price: SfTextView = view.price
        val quantity: SfTextView = view.quantity

        fun setItem(item: ListOrderResponceV3.Data.Item, isHideDivider: Boolean) {
            if (isHideDivider) divider.visibility = View.GONE

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                item.thumb,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            quantity.text =
                mActivity.getString(R.string.text_quantity, item.quantity.toString())

            name.text = item.displayNameDetail

            price.text = Functions.formatMoney(item.sell_price * item.quantity)
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}