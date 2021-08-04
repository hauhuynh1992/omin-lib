package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.util.FontUtils
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.bda.omnilibrary.views.flowTextView.FlowTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.suggesst_product_item.view.*


class SuggestProductAdapter(
    activity: Activity,
    list: ArrayList<Product>,
    private val onClickText: (product: Product) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<Product> = list
    private var mActivity: Activity = activity

    private var currentFocusPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.suggesst_product_item, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!mList[position].animation) {
            holder.itemView.visibility = View.VISIBLE

            ImageUtils.loadImage(
                mActivity,
                (holder as ItemViewHolder).imageCategory,
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

            holder.img_assers.visibility = View.GONE
            holder.img_freeship.visibility = View.GONE

            holder.ln_tag_value_1.visibility = View.GONE
            holder.name_tag_value_2.visibility = View.GONE

            if (mList.size == 1) {
                holder.rl_posistion.visibility = View.GONE
                holder.tv_posistion.visibility = View.GONE
            } else {
                holder.rl_posistion.visibility = View.VISIBLE
                holder.tv_posistion.visibility = View.VISIBLE
                holder.tv_posistion.text = (position + 1).toString()
            }
            @Suppress("SENSELESS_COMPARISON")
            if (mList[position].tags != null && mList[position].tags.size > 0) {
                for (tag in mList[position].tags) {
                    when (tag.tag_category) {
                        "assess" -> {
                            holder.img_assers.visibility = View.VISIBLE
                            ImageUtils.loadImage(mActivity, holder.img_assers, tag.image_promotion)
                        }
                        "shipping" -> {
                            holder.img_freeship.visibility = View.VISIBLE
                            ImageUtils.loadImage(
                                mActivity,
                                holder.img_freeship,
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
                onClickText(mList[position])
            }

            holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
                if (hasFocus) {
                    currentFocusPosition = position
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                } else {
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
        var tv_posistion: SfTextView = view.tv_posistion
        var rl_posistion: RelativeLayout = view.rl_posistion
        var img_freeship: ImageView = view.img_freeship
        var img_assers: ImageView = view.tv_assets
        val rl_con: RelativeLayout = view.rl_con

        val flow_name: FlowTextView = view.flow_name
        val ln_tag_value_1: LinearLayout = view.ln_tag_value_1
        val name_tag_value_1: SfTextView = view.name_tag_value_1
        val name_tag_value_2: SfTextView = view.name_tag_value_2
        val ic_tag_value_1: ImageView = view.ic_tag_value_1
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setData(data: ArrayList<Product>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }
}