package com.bda.omnilibrary.adapter.homev2

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.SimpleCollection
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_colection_v2.view.*

class CollectionAdapterV2(
    activity: BaseActivity,
    list: ArrayList<SimpleCollection>,
    positionOnList: Int
) : BaseAdapter(activity) {

    private var mList: ArrayList<SimpleCollection> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_colection_v2, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder)

        @Suppress("UselessCallOnNotNull")
        if (!mList[position].collection_icon.isNullOrBlank()) {
            ImageUtils.loadImage(
                mActivity,
                holder.imageCategory,
                mList[position].collection_icon,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )
        } else if (!mList[position].collection_image.isNullOrBlank()) {
            ImageUtils.loadImage(
                mActivity,
                holder.imageCategory,
                mList[position].collection_image,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )
        }

        holder.name.text = mList[position].collection_name
        try {
            holder.imageCategory.setColorFilter(Color.parseColor(mList[position].color_title))
            holder.name.setTextColor(Color.parseColor(mList[position].color_title))
            holder.rl_con.background = setStroke(mList[position].color_title)
        } catch (e: IllegalArgumentException) {

        } catch (ex: NullPointerException) {

        }



        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position], position)
        }

        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                clickListener.onPositionListFocus(mList[position], mPositionOnList, position)
                try {
                    holder.rl_con.background =
                        setGradient(mList[position].gradient_start, mList[position].gradient_end)
                } catch (ill: IllegalArgumentException) {
//run sợ content nhập liệu nên code như thế này
                } catch (ex: NullPointerException) {
//run sợ content nhập liệu nên code như thế này
                }
                holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.white))

                holder.imageCategory.setColorFilter(Color.WHITE)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        holder.layout_content_collection.outlineSpotShadowColor =
                            Color.parseColor(mList[position].color_title)
                    } catch (ill: IllegalArgumentException) {
//run sợ content nhập liệu nên code như thế này
                    } catch (ex: NullPointerException) {
//run sợ content nhập liệu nên code như thế này
                    }

                }
            } else {
                clickListener.onPositionLiveListFocus(mPositionOnList)


                try {
                    holder.rl_con.background = setStroke(mList[position].color_title)
                    holder.name.setTextColor(Color.parseColor(mList[position].color_title))
                    holder.imageCategory.setColorFilter(Color.parseColor(mList[position].color_title))
                } catch (e: IllegalArgumentException) {
//run sợ content nhập liệu nên code như thế này
                } catch (ex: NullPointerException) {
//run sợ content nhập liệu nên code như thế này
                }



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.layout_content_collection.outlineSpotShadowColor = Color.TRANSPARENT
                }
            }
        }
    }

    private fun setStroke(color: String): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        if (Config.platform == "box2018") {
            shape.setColor(ContextCompat.getColor(mActivity, R.color.color_item_background))
        } else {
            shape.setColor(Color.TRANSPARENT)
        }

        shape.cornerRadius = mActivity.resources.getDimensionPixelSize(R.dimen._6sdp).toFloat()
        shape.setStroke(
            mActivity.resources.getDimensionPixelSize(R.dimen._1sdp),
            Color.parseColor(color)
        )
        return shape
    }

    private fun setGradient(start: String, end: String): GradientDrawable {
        val shape = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(start), Color.parseColor(end))
        )
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = mActivity.resources.getDimensionPixelSize(R.dimen._6sdp).toFloat()

        return shape
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rl_con: RelativeLayout = view.rl_con
        val imageCategory: ImageView = view.image_category
        val name: SfTextView = view.name
        val layout_content_collection: MaterialCardView = view.layout_content_collection
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(collections: SimpleCollection, position: Int)
        fun onPositionListFocus(
            collections: SimpleCollection,
            positionEnterList: Int,
            position: Int
        )

        fun onPositionLiveListFocus(positionLiveList: Int)
    }
}