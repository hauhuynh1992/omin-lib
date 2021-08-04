package com.bda.omnilibrary.adapter.DetailProductAdapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.FontUtils
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.bda.omnilibrary.views.flowTextView.FlowTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_product_in_detail.view.*
import java.util.*


class DetailOrderProductAdapter(activity: BaseActivity, list: ArrayList<Product>) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list
    private lateinit var clickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_in_detail, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val relativeParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position != 0) {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._10sdp).toInt(),
                0,
                0,
                0
            )
            holder.itemView.layoutParams = relativeParams
        } else {
            relativeParams.setMargins(
                0,
                0,
                0,
                0
            )
            holder.itemView.layoutParams = relativeParams
        }

        holder as ItemViewHolder

        ImageUtils.loadImage(
            mActivity,
            holder.imageCategory,
            mList[position].imageCover,
            ImageUtils.TYPE_PRODUCT
        )

        holder.ln_tag_value_1.visibility = View.GONE
        holder.name_tag_value_2.visibility = View.GONE

        @Suppress("SENSELESS_COMPARISON")
        if (mList[position].tags != null && mList[position].tags.size > 0) {
            for (tag in mList[position].tags) {
                if (tag.tag_category == "assess") {
                    holder.img_assers.visibility = View.VISIBLE
                    ImageUtils.loadImage(mActivity, holder.img_assers, tag.image_promotion)
                } else if (tag.tag_category == "shipping") {
                    holder.img_freeship.visibility = View.VISIBLE
                    ImageUtils.loadImage(
                        mActivity,
                        holder.img_freeship,
                        tag.image_promotion
                    )
                } else if (tag.tag_category == "promotion" && tag.percentage != null && tag.percentage.isNotBlank()) {
                    if (tag.tag_type == "percentage") {
                        holder.img_promotion.visibility = View.VISIBLE
                        holder.img_promotion.text = tag.percentage.trim() + "%"

                    } else if (tag.tag_type == "fee" && tag.fee != null && tag.fee.isNotBlank()) {
                        holder.img_promotion.visibility = View.VISIBLE
                        holder.img_promotion.text = mActivity.getString(R.string.text_K, tag.fee.trim())//tag.fee.trim() + "K"
                    } else if (tag.tag_type == "extra_gift") {
                        holder.img_promotion.visibility = View.VISIBLE
                        holder.img_promotion.text = ""
                    }
                    ImageUtils.loadImageTextView(
                        mActivity,
                        holder.img_promotion,
                        tag.image_promotion
                    )
                } else if (tag.tag_category == "name_tag") {
                    if (tag.name_tag_value_1 != null && tag.name_tag_value_1 != "") {
                        holder.name_tag_value_1.text = "${tag.name_tag_value_1}%"
                        holder.ln_tag_value_1.visibility = View.VISIBLE
                    }

                    if (tag.name_tag_value_2 != null && tag.name_tag_value_2 != "") {
                        holder.name_tag_value_2.text = tag.name_tag_value_2
                        holder.name_tag_value_2.visibility = View.VISIBLE
                    }
                }
            }
        }

        holder.flow_name.setTypeface(FontUtils.getFontLatoBold(mActivity.assets))
        holder.flow_name.setText(mList[position].name)

        holder.salePrice.text = Functions.formatMoney(mList[position].price)

        if (mList[position].listedPrice > 0 && mList[position].listedPrice != mList[position].price) {
            holder.list_price.visibility = View.VISIBLE
            holder.list_price.text = Functions.formatMoney(mList[position].listedPrice)
        } else {
            holder.list_price.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }

        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {

                clickListener.onItemFocus(mList[position])

                Functions.setStrokeFocus(holder.layout_product_content, mActivity)


            } else {
                Functions.setLostFocus(holder.layout_product_content, mActivity)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: MaterialCardView = view.layout_product_content
        val imageCategory: ImageView = view.image_category
        val salePrice: SfTextView = view.sale_price
        val list_price: SfStrikeTextView = view.list_price
        var img_promotion: SfTextView = view.img_promotion
        var img_freeship: ImageView = view.img_freeship
        var img_assers: ImageView = view.tv_assets

        val rl_con: RelativeLayout = view.rl_con

        val flow_name: FlowTextView = view.flow_name
        val ln_tag_value_1: LinearLayout = view.ln_tag_value_1
        val name_tag_value_1: SfTextView = view.name_tag_value_1
        val ic_tag_value_1: ImageView = view.ic_tag_value_1
        val name_tag_value_2: SfTextView = view.name_tag_value_2
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemFocus(product: Product)
    }
}