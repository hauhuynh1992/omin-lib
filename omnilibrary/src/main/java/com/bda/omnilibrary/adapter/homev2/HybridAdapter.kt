package com.bda.omnilibrary.adapter.homev2

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.HybridSection
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_hybrid.view.*

class HybridAdapter(
    activity: BaseActivity,
    list: ArrayList<HybridSection>,
    positionOnList: Int = 1,
) : BaseAdapter(activity) {

    private var mList: ArrayList<HybridSection> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    private var currentFocusPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_hybrid, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemViewHolder
        holder.itemView.visibility = View.VISIBLE

        ImageUtils.loadImage(
            mActivity,
            holder.imageCategory,
            mList[position].image,
            ImageUtils.TYPE_HYBRID
        )

        val relativeParams =
            holder.rl_con.layoutParams as RecyclerView.LayoutParams
        /*if (position == 0) {
            relativeParams.setMargins(
                0,
                0,
                0,
                0
            )
        } else {
            relativeParams.setMargins(
                mActivity.resources.getDimensionPixelSize(R.dimen._1sdp),
                0,
                0,
                0
            )
        }*/
        holder.rl_con.layoutParams = relativeParams

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            holder.layout_product_content.outlineAmbientShadowColor =
                ContextCompat.getColor(mActivity, R.color.trans)
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position], position)
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            if (hasFocus) {
                currentFocusPosition = position
                Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                clickListener.onPositionListFocus(
                    mList[position],
                    mPositionOnList,
                    position
                )
            } else {

                Functions.setLostFocus(holder.layout_product_content, mActivity)

                clickListener.onPositionLiveListFocus(
                    mList[position],
                    mPositionOnList,
                    position
                )
            }
        }
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (position == mList.size) {
            R.layout.item_load_more
        } else {
            R.layout.item_product
        }
    }*/

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var layout_product_content: MaterialCardView = view.layout_content_collection
        val imageCategory: ImageView = view.image_category
        val rl_con: LinearLayout = view.rl_cons
    }

    override fun getItemCount() = mList.size

    fun getCurrentFocusPosition() = currentFocusPosition

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: HybridSection, position: Int)
        fun onPositionListFocus(product: HybridSection, positionEnterList: Int, position: Int)
        fun onPositionLiveListFocus(product: HybridSection, positionLiveList: Int, position: Int)
    }
}