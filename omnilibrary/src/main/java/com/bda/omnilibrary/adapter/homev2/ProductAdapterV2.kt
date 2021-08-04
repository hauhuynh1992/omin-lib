package com.bda.omnilibrary.adapter.homev2

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.item_brandshop_product.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_product.view.flow_name
import kotlinx.android.synthetic.main.item_product.view.ic_tag_value_1
import kotlinx.android.synthetic.main.item_product.view.image_category
import kotlinx.android.synthetic.main.item_product.view.img_freeship
import kotlinx.android.synthetic.main.item_product.view.img_promotion
import kotlinx.android.synthetic.main.item_product.view.layout_product_content
import kotlinx.android.synthetic.main.item_product.view.list_price
import kotlinx.android.synthetic.main.item_product.view.ln_tag_value_1
import kotlinx.android.synthetic.main.item_product.view.name_tag_value_1
import kotlinx.android.synthetic.main.item_product.view.name_tag_value_2
import kotlinx.android.synthetic.main.item_product.view.rl_con
import kotlinx.android.synthetic.main.item_product.view.sale_price
import kotlinx.android.synthetic.main.item_product.view.tv_assets

open class ProductAdapterV2(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = 1,
    private val isLoadMore: Boolean,
    private val collectionId: String = "",
    private val collectionName: String = "",
) : BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    private var currentFocusPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.item_load_more) {
            val v = getLayoutInflater()
                .inflate(R.layout.item_load_more, parent, false)
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            ItemWatchMore(v)
        } else {
            val v = getLayoutInflater()
                .inflate(R.layout.item_brandshop_product, parent, false)
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            ItemViewHolder(v)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemWatchMore) {
            val relativeParams =
                holder.rl_con.layoutParams as RecyclerView.LayoutParams
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._1sdp).toInt(),
                0,
                0,
                0
            )
            holder.rl_con.layoutParams = relativeParams

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                holder.layout_product_content.outlineAmbientShadowColor =
                    ContextCompat.getColor(mActivity, R.color.trans)
            }

            holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
                if (hasFocus) {
                    clickListener.onPositionListFocus(null, mPositionOnList, position)
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                    holder.img_arrow.setImageResource(R.mipmap.ic_arrow_right)
                } else {
                    Functions.setLostFocus(holder.layout_product_content, mActivity)
                    holder.img_arrow.setImageResource(R.mipmap.ic_arrow_right_black)
                    clickListener.onPositionLiveListFocus(null, mPositionOnList, position)

                }
            }

            holder.itemView.setOnClickListener {
                clickListener.onClickWatchMore(collectionId, collectionName)
            }

        } else if (holder is ItemViewHolder) {
            if (!mList[position].animation) {
                holder.itemView.visibility = View.VISIBLE

                ImageUtils.loadImage(
                    mActivity,
                    holder.imageCategory,
                    mList[position].imageCover,
                    ImageUtils.TYPE_PRODUCT
                )

                val relativeParams =
                    holder.rl_con.layoutParams as RecyclerView.LayoutParams
                if (position == 0) {
                    relativeParams.setMargins(
                        0,
                        0,
                        0,
                        0
                    )
                } else {
                    relativeParams.setMargins(
                        mActivity.resources.getDimension(R.dimen._1sdp).toInt(),
                        0,
                        0,
                        0
                    )
                }
                holder.rl_con.layoutParams = relativeParams

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_product_content.outlineAmbientShadowColor =
                        ContextCompat.getColor(mActivity, R.color.trans)
                }

                holder.ln_tag_value_1.visibility = View.GONE
                holder.name_tag_value_2.visibility = View.GONE

                holder.img_assers.visibility = View.GONE
                holder.ll_brand_skin.visibility = View.GONE
                holder.img_freeship.visibility = View.GONE
                holder.img_promotion.visibility = View.GONE

                mList[position].brand_shop?.let {
                    if (isHasBrandSkin(mList[position])) {
                        mList[position].brand_shop?.forEach {
                            holder.ll_brand_skin.visibility = View.VISIBLE
                            holder.tv_brand_skin.text = it.skin_name
                            ImageUtils.loadImage(
                                mActivity,
                                holder.img_logo,
                                it.skin_image,
                                ImageUtils.TYPE_BRAND_SHOP_AVATAR
                            )
                        }
                    } else {
                        holder.ll_brand_skin.visibility = View.GONE
                    }
                }

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
                                holder.img_promotion.text = tag.fee.trim() + "K"
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
                    clickListener.onItemClick(mList[position], position)
                }

                holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
                    if (hasFocus) {
                        currentFocusPosition = position
                        Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                        Functions.setBrandFocus(holder.layout_brand_skin, holder.tv_brand_skin.width + holder.img_logo.width + 100, holder.ic_tick, mActivity)
                        clickListener.onPositionListFocus(
                            mList[position],
                            mPositionOnList,
                            position
                        )
                    } else {

                        Functions.setLostFocus(holder.layout_product_content, mActivity)
                        Functions.setBrandLostFocus(holder.layout_brand_skin, holder.tv_brand_skin.width + holder.img_logo.width + 100, holder.ic_tick, mActivity)

                        clickListener.onPositionLiveListFocus(
                            mList[position],
                            mPositionOnList,
                            position
                        )
                    }
                }
            } else {
                holder.itemView.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mList.size && isLoadMore) {
            R.layout.item_load_more
        } else {
            R.layout.item_product
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var layout_product_content: MaterialCardView = view.layout_product_content
        var layout_brand_skin: MaterialCardView = view.layout_brand_skin
        var ll_brand_skin: LinearLayout = view.ll_brand_skin
        val imageCategory: ImageView = view.image_category
        val salePrice: SfTextView = view.sale_price
        val list_price: SfStrikeTextView = view.list_price
        var img_promotion: SfTextView = view.img_promotion
        var img_logo: ImageView = view.ic_logo
        var ic_tick: ImageView = view.ic_tick
        var tv_brand_skin: TextView = view.tv_brand_skin
        var img_freeship: ImageView = view.img_freeship
        var img_assers: ImageView = view.tv_assets
        val rl_con: RelativeLayout = view.rl_con

        val flow_name: FlowTextView = view.flow_name
        val ln_tag_value_1: LinearLayout = view.ln_tag_value_1
        val name_tag_value_1: SfTextView = view.name_tag_value_1
        val ic_tag_value_1: ImageView = view.ic_tag_value_1
        val name_tag_value_2: SfTextView = view.name_tag_value_2
    }

    @Suppress("PropertyName")
    inner class ItemWatchMore(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: MaterialCardView = view.layout_product_content
        val img_arrow: ImageView = view.img_arrow
        val rl_con: RelativeLayout = view.rl_con_
    }


    override fun getItemCount(): Int {
        return if (isLoadMore) {
            mList.size + 1
        } else {
            mList.size
        }
    }

    fun getCurrentFocusPosition() = currentFocusPosition

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }


    open fun isHasBrandSkin(product: Product): Boolean {
        var isHasTag = true
        product.tags.forEach {
            if (it.tag_category.equals("assess")) {
                return false
            }
        }
        if (product.brand_shop == null) {
            return false
        } else {
            product.brand_shop!!.forEach {
                if (it.skin_display_at_home) {
                    return true
                } else {
                    return false
                }
            }
        }
        return isHasTag
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product, position: Int)
        fun onPositionListFocus(product: Product?, positionEnterList: Int, position: Int)
        fun onPositionLiveListFocus(product: Product?, positionLiveList: Int, position: Int)
        fun onClickWatchMore(uid: String, name: String)
    }
}