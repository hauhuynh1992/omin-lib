package com.bda.omnilibrary.adapter.DetailProductAdapter

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.detail_present_item.view.*
import kotlinx.android.synthetic.main.item_button_media.view.*
import java.util.*


class DetailPresentAdapter(
    activity: BaseActivity,
    list: ArrayList<Product.MediaType>,
    private val isDisabledQuickPay: Boolean
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product.MediaType> = list
    private lateinit var focusListener: OnFocusChangeListener
    private var isPlayVideo: Boolean = true

    var bnBuyNowExport: LinearLayout? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == R.layout.item_button_media) {
            val v = getLayoutInflater()
                .inflate(R.layout.item_button_media, parent, false)
            ButtonViewHolder(v)
        } else {
            val v = getLayoutInflater()
                .inflate(R.layout.detail_present_item, parent, false)
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            ViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val layoutParams1 = holder.rl_image.layoutParams
            if (mList[position].mediaType == "video") {
                layoutParams1.width = mActivity.resources.getDimension(R.dimen._144sdp).toInt()

                holder.img_play.visibility =
                    View.VISIBLE
                ImageUtils.loadImage(
                    mActivity,
                    holder.img_item_detail,
                    mList[position].icon,
                    ImageUtils.TYPE_VIDEO
                )
            } else {
                if (mList[position].square) {
                    layoutParams1.width = mActivity.resources.getDimension(R.dimen._81sdp).toInt()
                } else {
                    layoutParams1.width = mActivity.resources.getDimension(R.dimen._144sdp).toInt()
                }
                holder.img_play.visibility =
                    View.GONE
                ImageUtils.loadImage(
                    mActivity,
                    holder.img_item_detail,
                    mList[position].icon,
                    ImageUtils.TYPE_PRIVIEW_SMALL
                )
            }

            holder.rl_image.layoutParams = layoutParams1

            holder.itemView.setOnClickListener {
                if (mList[position].mediaType == "video") {
                    isPlayVideo = !isPlayVideo
                    if (isPlayVideo) {
                        holder.img_play.setImageResource(R.mipmap.ic_pause_black)
                        holder.img_play.setColorFilter(Color.WHITE)
                    } else {
                        holder.img_play.setImageResource(R.mipmap.ic_play_white)
                    }
                    focusListener.onItemVideoClick(position, isPlayVideo)
                }
            }

            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                focusListener.onItemFocus(position, hasFocus)
                if (hasFocus) {
                    holder.img_play.setImageResource(R.mipmap.ic_pause_black)
                    holder.img_play.setColorFilter(Color.WHITE)

                    Functions.setStrokeFocus(holder.item_category, mActivity)
                } else {
                    holder.img_play.setImageResource(R.mipmap.ic_play_white)
                    Functions.setLostFocus(holder.item_category, mActivity)
                }
            }
        } else if (holder is ButtonViewHolder) {

            if (isDisabledQuickPay)
                holder.bn_buy_now.visibility = View.GONE

            // button
            holder.bn_back.setOnClickListener {
                focusListener.onClickBack()
            }

            holder.bn_buy_now.setOnClickListener {
                focusListener.onClickBuyNow()
            }

            holder.bn_buy_now.setOnFocusChangeListener { _, hasFocus ->
                holder.text_bn_buy_now.isSelected = hasFocus
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        holder.item_bn_buy_now.outlineSpotShadowColor =
                            ContextCompat.getColor(mActivity, R.color.color_shadow)
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        holder.item_bn_buy_now.outlineSpotShadowColor =
                            ContextCompat.getColor(mActivity, R.color.trans)
                    }
                }
            }

            holder.bn_back.setOnFocusChangeListener { _, hasFocus ->
                holder.text_bn_back.isSelected = hasFocus
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        holder.item_bn_back.outlineSpotShadowColor =
                            ContextCompat.getColor(mActivity, R.color.color_shadow)
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        holder.item_bn_back.outlineSpotShadowColor =
                            ContextCompat.getColor(mActivity, R.color.trans)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.size == position) {
            R.layout.item_button_media
        } else {
            R.layout.detail_present_item
        }
    }

    @Suppress("PropertyName")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_item_detail: ImageView = view.img_item_detail
        var img_play: ImageView = view.img_play
        var item_category: MaterialCardView = view.item_category
        val rl_image: RelativeLayout = view.rl_image
    }

    @Suppress("PropertyName")
    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_buy_now: LinearLayout = view.bn_buy_now
        val item_bn_buy_now: MaterialCardView = view.item_bn_buy_now
        val text_bn_buy_now: SfTextView = view.text_bn_buy_now

        val bn_back: LinearLayout = view.bn_back
        val item_bn_back: MaterialCardView = view.item_bn_back
        val text_bn_back: SfTextView = view.text_bn_back

        init {
            bnBuyNowExport = bn_buy_now
        }
    }


    override fun getItemCount(): Int {
        return mList.size + 1
    }

    fun setOnItemFocusListener(focusListener: OnFocusChangeListener) {
        this.focusListener = focusListener
    }

    interface OnFocusChangeListener {
        fun onItemFocus(position: Int, hasFocus: Boolean)
        fun onItemVideoClick(position: Int, isPlayVideo: Boolean)
        fun onClickBuyNow()
        fun onClickBack()
    }
}