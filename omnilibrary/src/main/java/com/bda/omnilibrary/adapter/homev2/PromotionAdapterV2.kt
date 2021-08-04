package com.bda.omnilibrary.adapter.homev2

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.model.Promotion
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_promotion.view.*
import java.util.*

class PromotionAdapterV2(
    activity: BaseActivity,
    list: ArrayList<Promotion>,
    positionOnList: Int = 1
) : BaseAdapter(activity) {

    private var mList: ArrayList<Promotion> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_promotion, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.visibility = View.VISIBLE
        ImageUtils.loadImage(
            mActivity,
            (holder as ItemViewHolder).imageCategory,
            mList[position].image_cover,
            ImageUtils.TYPE_PRODUCT
        )

        holder.name.text = mList[position].display_name


        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position], position)
        }

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

        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            if (hasFocus) {
                Functions.setStrokeFocus(holder.layout_product_content, mActivity)

                clickListener.onPositionListFocus(mList[position],mPositionOnList)

            } else {
                Functions.setLostFocus(holder.layout_product_content, mActivity)

                clickListener.onPositionLiveListFocus(mList[position], mPositionOnList)

            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: MaterialCardView = view.layout_product_content
        val imageCategory: ImageView = view.image_category
        val name: SfTextView = view.name
        val rl_con: RelativeLayout = view.rl_con
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: Promotion, position: Int)
        fun onPositionListFocus(product: Promotion, positionEnterList: Int)
        fun onPositionLiveListFocus(product: Promotion, positionLiveList: Int)
    }
}