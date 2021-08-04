package com.bda.omnilibrary.adapter.homev2

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_market_assistant.view.*
import java.util.*

class MarketAssistantAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = 1
) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_market_assistant, parent, false)
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
                ImageUtils.TYPE_SHOW
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


            holder.name.text = mList[position].name

            holder.salePrice.text = Functions.formatMoney(mList[position].price)

            if (mList[position].listedPrice > 0 && mList[position].listedPrice != mList[position].price) {
                holder.list_price.visibility = View.VISIBLE
                holder.list_price.text = Functions.formatMoney(mList[position].listedPrice)
            } else {
                holder.list_price.visibility = View.GONE
            }

            val adapter = ProductLiteAdapter(mActivity, mList, 1)
            holder.list.adapter = adapter

            holder.itemView.setOnClickListener {
                clickListener.onItemClick(mList[position], position)
            }

            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)

                    clickListener.onPositionListFocus(mPositionOnList)

                } else {
                    Functions.setLostFocus(holder.layout_product_content, mActivity)

                    clickListener.onPositionLiveListFocus(mPositionOnList)

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

        val name: SfTextView = view.name

        val salePrice: SfTextView = view.price
        val list_price: SfStrikeTextView = view.list_price
        val option: SfTextView = view.option

        val list: RecyclerView = view.list

        val rl_con: RelativeLayout = view.rl_con

        init {
            list.layoutManager =
                LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            list.isEnabled = false
        }
    }


    override fun getItemCount(): Int {
        return 2
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