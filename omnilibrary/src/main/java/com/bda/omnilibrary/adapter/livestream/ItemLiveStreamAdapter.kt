package com.bda.omnilibrary.adapter.livestream

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_product_livestream.view.*

class ItemLiveStreamAdapter(
    activity: BaseActivity,
    list: List<LiveStream.StreamProduct>,
    positionOnList: Int
) : BaseAdapter(activity) {
    private var mList: List<LiveStream.StreamProduct> = list
    private lateinit var focusListener: OnFocusChangeListener
    private var isClicked = false
    private val mPositionOnList: Int = positionOnList
    private var currentClickViewHolder: ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_livestream, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder

        ImageUtils.loadImage(
            mActivity,
            holder.image_category,
            mList[position].product.imageCover,
            ImageUtils.TYPE_BRAND_SHOP_AVATAR
        )

        holder.name.text = mList[position].product.name
        holder.sale_price.text = Functions.formatMoney(mList[position].product.price)
        holder.list_price.text = Functions.formatMoney(mList[position].product.listedPrice)

        holder.itemView.setOnClickListener {
            focusListener.onItemProduct(mList[position], position)
            isClicked = true
            currentClickViewHolder = holder
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            focusListener.onFocus(position, hasFocus)

            if (hasFocus) {
                focusListener.onPositionListFocus(mList[position], position, mPositionOnList)
                isClicked = false

                if (currentClickViewHolder != null) {
                    Functions.setLostFocus(
                        currentClickViewHolder!!.layout_product_content,
                        mActivity
                    )
                    currentClickViewHolder = null
                }

                Functions.setThinStrokeFocus(holder.layout_product_content, mActivity)
            } else {
                focusListener.onPositionLiveListFocus(mPositionOnList)
                if (!isClicked)
                    Functions.setLostFocus(holder.layout_product_content, mActivity)
            }
        }

        if (mList[position].isHighlight) {
            holder.rl_content.background =
                ContextCompat.getDrawable(mActivity, R.drawable.gradient_orange)
            holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            holder.sale_price.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            holder.list_price.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            holder.list_price.onPreDraw()
        } else {
            holder.rl_content.setBackgroundColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.color_product_background
                )
            )

            holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.text_color_dark))
            holder.sale_price.setTextColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.color_41AE96
                )
            )
            holder.list_price.setTextColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.text_color_gray
                )
            )
            holder.list_price.onPreDraw()
        }
    }

    @Suppress("PropertyName")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val rl_content: LinearLayout = view.rl_content
        val layout_product_content: MaterialCardView = view.layout_product_content
        val name: SfTextView = view.name
        val sale_price: SfTextView = view.sale_price
        val list_price: SfStrikeTextView = view.list_price
        val image_category: ImageView = view.image_category
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemFocusListener(focusListener: OnFocusChangeListener) {
        this.focusListener = focusListener
    }

    interface OnFocusChangeListener {
        fun onItemProduct(product: LiveStream.StreamProduct, position: Int)
        fun onPositionListFocus(product: LiveStream.StreamProduct, position: Int,  positionEnterList: Int)
        fun onPositionLiveListFocus(positionLiveList: Int)
        fun onFocus(position: Int, hasFocus: Boolean)
    }

}