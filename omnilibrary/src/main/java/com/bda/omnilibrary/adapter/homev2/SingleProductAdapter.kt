package com.bda.omnilibrary.adapter.homev2

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.custome.VideoPlayer
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.FontUtils
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.bda.omnilibrary.views.flowTextView.FlowTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_single_product.view.*
import java.util.*

class SingleProductAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = 1
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener
    private var handler = Handler()
    private var runnable: Runnable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_single_product, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    @Suppress("SENSELESS_COMPARISON")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!mList[position].animation) {
            holder.itemView.visibility = View.VISIBLE

            ImageUtils.loadImage(
                mActivity,
                (holder as ItemViewHolder).imageCategory,
                mList[position].imageCover,
                ImageUtils.TYPE_PRODUCT
            )

            holder.ln_tag_value_1.visibility = View.GONE
            holder.name_tag_value_2.visibility = View.GONE
            holder.img_assers.visibility = View.GONE
            holder.img_freeship.visibility = View.GONE
            holder.img_promotion.visibility = View.GONE

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
                                holder.img_promotion.text = tag.percentage?.trim() + "%"

                            } else if (tag.tag_type == "fee") {
                                holder.img_promotion.visibility = View.VISIBLE
                                holder.img_promotion.text = mActivity.getString(
                                    R.string.text_K,
                                    tag.fee.trim()
                                )//tag.fee.trim() + "K"
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
                if (runnable != null) {
                    handler.removeCallbacks(runnable!!)
                    runnable = null
                }
                holder.video.pause()
                System.gc()
                holder.video.release()
                clickListener.onItemClick(mList[position], position)
            }
            if(mList[position].videos.isNotEmpty()) {
                ImageUtils.loadImage(
                    mActivity,
                    holder.bg_thumb,
                    mList[position].videos[0].icon,
                    ImageUtils.TYPE_VIDEO_SINGLE
                )
            }
            holder.bg_thumb.visibility = View.VISIBLE


            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {

                    holder.ic_play.visibility = View.VISIBLE
                    clickListener.onPositionListFocus(mList[position], mPositionOnList, position)
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                    if (mList[position].videos.isNotEmpty()) {
                        if (runnable != null) {
                            handler.removeCallbacks(runnable!!)
                            runnable = null
                        }
                        runnable = Runnable {
                            holder.video.setMute(false)

                            val video = Functions.isVideoInProduct(mList[position])
                            video?.let {
                                holder.ic_play.visibility = View.GONE
                                holder.progressBar7.visibility = View.VISIBLE
                                holder.video.start(Functions.getVideoUrl(video))
                                holder.video.setShowSpinner(false)
                                holder.video.setVideoTracker(object :
                                    VideoPlayer.VideoPlaybackTracker {
                                    override fun onPlaybackError(e: Exception?) {

                                    }

                                    override fun onCompleteVideo() {
                                        holder.bg_thumb.visibility = View.VISIBLE
                                        holder.bg_thumb.alpha=1f
                                        holder.ic_play.visibility = View.VISIBLE
//                                        Functions.alphaAnimation(holder.bg_thumb, 1f, 500L) {
//                                            holder.ic_play.visibility = View.VISIBLE
//                                        }
                                    }

                                    override fun onTimeStick(time: Int) {

                                    }

                                    override fun onReady() {
                                        Functions.alphaAnimation(holder.bg_thumb, 0f, 500L) {
                                            holder.progressBar7.visibility = View.GONE
                                            holder.bg_thumb.visibility = View.GONE
                                        }
                                    }

                                })
                            }

                        }
                        handler.postDelayed(runnable!!, 1000)
                    }

                } else {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable!!)
                        runnable = null
                    }
                    if (mList[position].videos.isNotEmpty()) {
                        holder.ic_play.visibility = View.VISIBLE
                        holder.bg_thumb.visibility = View.VISIBLE
                        holder.progressBar7.visibility = View.GONE
                        holder.bg_thumb.alpha=1f
//                        Functions.alphaAnimation(holder.bg_thumb, 1f, 500L) {
//
//                        }
                        holder.video.pause()
                        holder.video.release()
                    }

                    Functions.setLostFocus(holder.layout_product_content, mActivity)
                    clickListener.onPositionLiveListFocus(mPositionOnList)
                }
            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    fun setStrokeFocus(view: MaterialCardView, mActivity: Activity) {
        view.cardElevation =
            mActivity.resources.getDimension(R.dimen._4sdp)

        view.strokeWidth = 8
        view.strokeColor =
            ContextCompat.getColor(mActivity, R.color.start_color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                ContextCompat.getColor(mActivity, R.color.end_color)
        }
    }

    fun setLostFocus(view: MaterialCardView, mActivity: Activity) {
        view.cardElevation =
            mActivity.resources.getDimension(R.dimen._2sdp)

        view.strokeWidth = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                ContextCompat.getColor(mActivity, R.color.text_black_70)
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
        val video: VideoPlayer = view.video
        val ic_play: ImageView = view.ic_play
        val bg_thumb: ImageView = view.bg_thumb
        val flow_name: FlowTextView = view.flow_name
        val ln_tag_value_1: LinearLayout = view.ln_tag_value_1
        val name_tag_value_1: SfTextView = view.name_tag_value_1
        val name_tag_value_2: SfTextView = view.name_tag_value_2
        val ic_tag_value_1: ImageView = view.ic_tag_value_1
        val progressBar7: ProgressBar = view.progressBar7
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