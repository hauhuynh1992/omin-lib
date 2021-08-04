package com.bda.omnilibrary.adapter.homev2

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_product_lite.view.*
import java.util.*

class ProductLiteAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = 1
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_lite, parent, false)
        v.isFocusable = false
        v.isFocusableInTouchMode = false
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        ImageUtils.loadImage(
            mActivity,
            (holder as ItemViewHolder).imageCategory,
            mList[position].imageCover,
            ImageUtils.TYPE_PRIVIEW_SMALL
        )

        holder.name.text = mList[position].name

        holder.salePrice.text = Functions.formatMoney(mList[position].price)

        if (mList[position].listedPrice > 0 && mList[position].listedPrice != mList[position].price) {
            holder.list_price.visibility = View.VISIBLE
            holder.list_price.text = Functions.formatMoney(mList[position].listedPrice)
        } else {
            holder.list_price.visibility = View.GONE
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: CardView = view.layout_product_content
        val imageCategory: ImageView = view.image_category
        val name: SfTextView = view.name
        val salePrice: SfTextView = view.sale_price
        val list_price: SfStrikeTextView = view.list_price
    }


    override fun getItemCount(): Int {
        return 4
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product, position: Int)
        fun onPositionListFocus(positionEnterList: Int)
        fun onPositionLiveListFocus(positionLiveList: Int)
    }
}