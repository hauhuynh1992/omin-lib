package com.bda.omnilibrary.adapter.homev2

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
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
import kotlinx.android.synthetic.main.item_best_seller.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import java.util.*

class BestSellerAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = -1,
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_best_seller, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!mList[position].animation) {
            holder.itemView.visibility = View.VISIBLE

            ImageUtils.loadImage(
                mActivity,
                (holder as ItemViewHolder).imageCategory,
                mList[position].imageCover,
                ImageUtils.TYPE_PRODUCT
            )

            when (position) {
                0 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_1)

                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._138sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._218sdp)
                }

                1 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_2)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._218sdp)
                }

                2 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_3)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._218sdp)
                }

                3 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_4)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._235sdp)
                }

                4 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_5)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._212sdp)
                }

                5 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_6)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._213sdp)

                }

                6 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_7)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._210sdp)

                }

                7 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_8)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._228sdp)

                }

                8 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_9)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._179sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._213sdp)
                }

                9 -> {
                    holder.img_number.setImageResource(R.mipmap.ic_img_number_10)
                    holder.img_number.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._245sdp)

                    holder.rl_con.layoutParams.width =
                        mActivity.resources.getDimensionPixelSize(R.dimen._300sdp)
                }
            }

            holder.img_assers.visibility = View.GONE
            holder.img_freeship.visibility = View.GONE
            holder.img_promotion.visibility = View.GONE

            holder.ln_tag_value_1.visibility = View.GONE
            holder.name_tag_value_2.visibility = View.GONE

            @Suppress("SENSELESS_COMPARISON")
            @SuppressLint("SetTextI18n")
            if (mList[position].tags != null && mList[position].tags.size > 0) {

                for (tag in mList[position].tags) {
                    when (tag.tag_category) {
                        "assess" -> {
                            holder.img_freeship.visibility = View.VISIBLE
                            ImageUtils.loadImage(mActivity, holder.img_assers, tag.image_promotion)
                        }
                        "shipping" -> {
                            holder.img_assers.visibility = View.VISIBLE
                            ImageUtils.loadImage(
                                mActivity,
                                holder.img_freeship,
                                tag.image_promotion
                            )
                        }
                        "promotion" -> {
                            if (tag.tag_type == "percentage") {
                                holder.img_promotion.visibility = View.VISIBLE
                                holder.img_promotion.text = tag.percentage.trim() + "%"

                            } else if (tag.tag_type == "fee" && tag.fee != null) {
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
                        }
                        "name_tag" -> {
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


            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {
                    clickListener.onPositionListFocus(mList[position], mPositionOnList, position)
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)

                } else {

                    clickListener.onPositionLiveListFocus(mPositionOnList)
                    Functions.setLostFocus(holder.layout_product_content, mActivity)
                }
            }
        } else {
            holder.itemView.visibility = View.GONE
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
        val img_number: AppCompatImageView = view.img_number
        val rl_con: RelativeLayout = view.rl_con_

        val flow_name: FlowTextView = view.flow_name
        val ln_tag_value_1: LinearLayout = view.ln_tag_value_1
        val name_tag_value_1: SfTextView = view.name_tag_value_1
        val name_tag_value_2: SfTextView = view.name_tag_value_2
        val ic_tag_value_1: ImageView = view.ic_tag_value_1

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: Product, position: Int)
        fun onPositionListFocus(product: Product, positionEnterList: Int, position: Int)
        fun onPositionLiveListFocus(positionLiveList: Int)
    }
}