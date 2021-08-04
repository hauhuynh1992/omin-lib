package com.bda.omnilibrary.ui.liveStreamActivity.AddedListToCart

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier_product_live_stream.view.*

class ItemProductAdapter(
    val activity: BaseActivity,
    val mList: List<Product>
) : BaseAdapter(activity) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier_product_live_stream, parent, false)
        v.isFocusable = false
        v.isFocusableInTouchMode = false
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).setData(mList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val price: SfTextView = view.price
        val image: ImageView = view.image
        val name: SfTextView = view.name
        val quantity: SfTextView = view.quantity

        fun setData(product: Product) {
            name.text = product.display_name_detail
            price.text = Functions.formatMoney(product.price)
            ImageUtils.loadImage(activity, image, product.imageCover, ImageUtils.TYPE_PRIVIEW_SMALL)
            quantity.text =
                activity.getString(R.string.product_quantity, product.order_quantity.toString())
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}