package com.bda.omnilibrary.adapter.homev2

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.BrandShop
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_hot_store.view.*
import java.util.*

class HotStoreAdapter(
    activity: BaseActivity,
    list: ArrayList<BrandShop>,
    positionOnList: Int = 1
) : BaseAdapter(activity) {

    private var mList: ArrayList<BrandShop> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_hot_store, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.visibility = View.VISIBLE

        ImageUtils.loadImage(
            mActivity,
            (holder as ItemViewHolder).imageCategory,
            mList[position].image_banner,
            ImageUtils.TYPE_BRAND_SHOP_IMAGE
        )

        ImageUtils.loadImageCache(
            mActivity,
            (holder).profile_image,
            mList[position].image_logo,
            ImageUtils.TYPE_PRIVIEW_SMALL
        )

        if (mList[position].certification_tag)
            holder.tv_rippon.visibility = View.VISIBLE
        else
            holder.tv_rippon.visibility = View.GONE

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

        holder.product_count.text = Functions.formatCount(mList[position].product_counts)

        holder.name.text = mList[position].name

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position], position)
        }

        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                Functions.setStrokeFocus(holder.layout_product_content, mActivity)

                clickListener.onPositionListFocus(mList[position], position, mPositionOnList)

                clickListener.onFocusItem(mList[position], position)
            } else {
                Functions.setLostFocus(holder.layout_product_content, mActivity)

                clickListener.onPositionLiveListFocus(mPositionOnList)

                clickListener.onLostFocusItem(mList[position])
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var layout_product_content: MaterialCardView = view.layout_product_content
        val imageCategory: ImageView = view.image_category
        val name: SfTextView = view.name
        val product_count: SfTextView = view.product_count

        var tv_rippon: AppCompatImageView = view.tv_rippon
        val rating: RatingBar = view.rating

        val profile_image: CircleImageView = view.profile_image

        val rl_con: RelativeLayout = view.rl_con
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: BrandShop, position: Int)
        fun onPositionListFocus(product: BrandShop, position: Int, positionEnterList: Int)
        fun onPositionLiveListFocus(positionLiveList: Int)

        fun onFocusItem(brandShop: BrandShop, index: Int)
        fun onLostFocusItem(brandShop: BrandShop)
    }
}