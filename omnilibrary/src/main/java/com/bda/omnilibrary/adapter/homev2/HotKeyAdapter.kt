package com.bda.omnilibrary.adapter.homev2

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_hot_key.view.*

class HotKeyAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_hot_key, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val relativeParams =
            (holder as ItemViewHolder).rl_con.layoutParams as RecyclerView.LayoutParams

        if (position != 0) {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._12sdp).toInt(),
                0,
                0,
                0
            )
        } else {
            relativeParams.setMargins(
                mActivity.resources.getDimension(R.dimen._3sdp).toInt(),
                0,
                0,
                0
            )
        }
        holder.rl_con.layoutParams = relativeParams

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position], position)
        }

        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                clickListener.onPositionListFocus(mPositionOnList)
                holder.rl_con.background =
                    ContextCompat.getDrawable(mActivity, R.drawable.background_live_item)
                holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            } else {
                clickListener.onPositionLiveListFocus(mPositionOnList)
                holder.rl_con.background = ContextCompat.getDrawable(
                    mActivity,
                    R.drawable.background_collection_v2_default
                )
                holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.start_color))
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rl_con: RelativeLayout = view.rl_con
        val name: SfTextView = view.name
    }

    override fun getItemCount(): Int {
        return 8
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