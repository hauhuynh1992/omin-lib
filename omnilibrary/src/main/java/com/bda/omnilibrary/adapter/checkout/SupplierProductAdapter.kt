package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier_product.view.*

class SupplierProductAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
) : BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier_product, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            //todo
            holder.setProduct(product = mList[position], isHideDivider = position == 0)
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

        fun setProduct(product: Product, isHideDivider: Boolean) {
            if (isHideDivider) divider.visibility = View.GONE

            quantity.text =
                mActivity.getString(R.string.text_quantity, product.order_quantity.toString())

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                product.imageCover,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            name.text = product.display_name_detail

            price.text = Functions.formatMoney(product.price * product.order_quantity)
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}