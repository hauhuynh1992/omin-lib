package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_product_checkout.view.*

class ProductCheckoutAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>?,
    private val items: ArrayList<ListOrderResponce.Data.Item>? = null
) : BaseAdapter(activity) {

    private var mList: ArrayList<Product>? = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_checkout, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {

            if (position == 3) {
                holder.setMoreProduct()

                if (mList != null) {
                    holder.more_product.text =
                        mActivity.getString(
                            R.string.more_product_checkout,
                            (mList!!.size - 3).toString()
                        )


                } else if (items != null) {
                    holder.more_product.text =
                        mActivity.getString(
                            R.string.more_product_checkout,
                            (items.size - 3).toString()
                        )

                }

            } else {

                if (mList != null) {
                    ImageUtils.loadImage(
                        mActivity,
                        holder.img_thumbnail,
                        mList!![position].imageCover,
                        ImageUtils.TYPE_PRIVIEW_SMALL
                    )

                    holder.name.text = mList!![position].display_name_detail


                } else if (items != null) {
                    ImageUtils.loadImage(
                        mActivity,
                        holder.img_thumbnail,
                        items[position].thumb,
                        ImageUtils.TYPE_PRIVIEW_SMALL
                    )

                    holder.name.text = items[position].displayNameDetail

                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: LinearLayout = view.content
        val img_thumbnail: ImageView = view.img_thumbnail
        val name: SfTextView = view.name

        val more_product: SfTextView = view.more_product

        fun setMoreProduct() {
            content.visibility = View.GONE

            more_product.visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int {
        when {
            mList != null -> {
                return if (mList!!.size > 3) {
                    4
                } else {
                    mList!!.size
                }
            }
            items != null -> {
                return if (items.size > 3) {
                    4
                } else {
                    items.size
                }
            }
            else -> return 0
        }
    }
}