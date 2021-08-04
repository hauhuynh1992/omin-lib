package com.bda.omnilibrary.adapter.homev2

import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.custome.VideoPlayer
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_live.view.*
import kotlinx.android.synthetic.main.item_live.view.image_category
import kotlinx.android.synthetic.main.item_live.view.layout_product_content
import kotlinx.android.synthetic.main.item_live.view.ln_view_count
import kotlinx.android.synthetic.main.item_live.view.rl_con
import kotlinx.android.synthetic.main.item_live.view.txt_view
import kotlinx.android.synthetic.main.item_single_livestream.view.*
import kotlinx.android.synthetic.main.item_thumbnail_livestream.view.*

class LiveAdapter(
    activity: BaseActivity,
    list: List<LiveStream>,
    positionOnList: Int,
    private var layoutId: Int? = LIVESTREAM_LAYOUT_MULTI
) : BaseAdapter(activity) {

    private var mList: List<LiveStream> = list
    private var mPositionOnList = positionOnList
    private lateinit var clickListener: OnItemClickListener
    private var checkForUserPauseAndSpeak = Handler()

    private var handler = Handler()
    private var runnable: Runnable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutId == LIVESTREAM_LAYOUT_MULTI) {
            val v = getLayoutInflater()
                .inflate(R.layout.item_live, parent, false)
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            return ItemMultiViewHolder(v)
        } else {
            val v = getLayoutInflater()
                .inflate(R.layout.item_single_livestream, parent, false)
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            return ItemSingleViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (layoutId == LIVESTREAM_LAYOUT_MULTI) {
            val relativeParams =
                (holder as ItemMultiViewHolder).rl_con.layoutParams as RecyclerView.LayoutParams
            if (position != 0) {
                relativeParams.setMargins(
                    mActivity.resources.getDimension(R.dimen._1sdp).toInt(),
                    0,
                    0,
                    0
                )
                holder.rl_con.layoutParams = relativeParams
            } else {
                relativeParams.setMargins(
                    0,
                    0,
                    0,
                    0
                )
                holder.rl_con.layoutParams = relativeParams
            }

            ImageUtils.loadImage(
                mActivity,
                holder.imageCategory,
                mList[position].image_thumb,
                ImageUtils.TYPE_HOTDEAL
            )

            holder.itemView.setOnClickListener {
                checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
                holder.videoPlayer.pause()
                Handler().postDelayed({
                    System.gc()
                    holder.videoPlayer.release()
                }, 3000)

                clickListener.onItemClick(mList[position], position)

            }

            holder.tv_name.text = mList[position].display_name

            if (mList[position].status == 1) {
                holder.ln_view_count.visibility = View.VISIBLE
                holder.txt_view.text = mActivity.getString(R.string.text_title_live)
            } else {
                holder.ln_view_count.visibility = View.GONE
            }
            holder.videoPlayer.setMute(true)

            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                    clickListener.onPositionListFocus(mList[position],position,  mPositionOnList)
                } else {
                    Functions.setLostFocus(holder.layout_product_content, mActivity)
                    clickListener.onPositionLiveListFocus(mPositionOnList)


                }
            }
        } else {
            holder.itemView.visibility = View.VISIBLE
            ImageUtils.loadImage(
                mActivity,
                (holder as ItemSingleViewHolder).imageCategory,
                mList[position].image_thumb,
                ImageUtils.TYPE_PRODUCT
            )

            holder.tvName.text = mList[position].display_name
            if (mList[position].status == 1) {
                holder.ln_view_count.visibility = View.VISIBLE
                holder.txt_view.text = mActivity.getString(R.string.text_title_live)
            } else {
                holder.ln_view_count.visibility = View.GONE
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
            holder.bg_thumb.visibility = View.VISIBLE
            ImageUtils.loadImage(
                mActivity,
                holder.bg_thumb,
                mList[position].image_fullscreen,
                ImageUtils.TYPE_VIDEO_SINGLE
            )

            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {
                    clickListener.onPositionListFocus(mList[position], position, mPositionOnList)
                    Functions.setStrokeFocus(holder.layout_product_content, mActivity)
                    if (mList[position].status == 1) {
                        holder.ic_play.visibility = View.GONE
                    } else {
                        holder.ic_play.visibility = View.GONE

                        if (runnable != null) {
                            handler.removeCallbacks(runnable!!)
                            runnable = null
                        }
                        runnable = Runnable {
                            val video = mList[position].video_transcode
                            holder.progressBar7.visibility = View.VISIBLE
                            holder.video?.setScaleFit(true)
                            holder.video?.setShowSpinner(false)
                            holder.video?.setFadeInTime(true)
                            holder.video?.start(video)
                            holder.video?.setVideoTracker(object :
                                VideoPlayer.VideoPlaybackTracker {
                                override fun onPlaybackError(e: Exception?) {
                                    holder.progressBar7.visibility = View.GONE
                                }

                                override fun onCompleteVideo() {
                                    holder.bg_thumb.visibility = View.VISIBLE
                                    holder.bg_thumb.alpha = 1f
                                    holder.ic_play.visibility = View.VISIBLE
                                    holder.progressBar7.visibility = View.GONE
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
                        handler.postDelayed(runnable!!, 1000)
                    }
                } else {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable!!)
                        runnable = null
                    }
                    holder.ic_play.visibility = View.VISIBLE
                    holder.bg_thumb.visibility = View.VISIBLE
                    holder.progressBar7.visibility = View.GONE
                    holder.bg_thumb.alpha = 1f
                    Functions.alphaAnimation(holder.bg_thumb, 1f, 500L) {

                    }
                    holder.video.pause()
                    holder.video.release()
                    Functions.setLostFocus(holder.layout_product_content, mActivity)
                    clickListener.onPositionLiveListFocus(mPositionOnList)
                }
            }
        }

    }

    @Suppress("PropertyName")
    inner class ItemMultiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: MaterialCardView = view.layout_product_content
        val imageCategory: ImageView = view.image_category
        val tv_name: SfTextView = view.tv_name
        val videoPlayer: VideoPlayer = view.videoPlayer
        val rl_con: RelativeLayout = view.rl_con
        val ln_view_count: RelativeLayout = view.ln_view_count
        val txt_view: SfTextView = view.txt_view
    }

    @Suppress("PropertyName")
    inner class ItemSingleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_product_content: MaterialCardView = view.layout_product_content
        val tvName: SfTextView = view.tv_livestream_name
        val imageCategory: ImageView = view.image_category
        val ln_view_count: RelativeLayout = view.ln_view_count
        val txt_view: SfTextView = view.txt_view
        val video: VideoPlayer = view.video
        val ic_play: ImageView = view.ic_play
        val bg_thumb: ImageView = view.bg_thumb
        val progressBar7: ProgressBar = view.progressBar7
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(product: LiveStream, position: Int)
        fun onPositionListFocus(product: LiveStream, position: Int,positionEnterList: Int)
        fun onPositionLiveListFocus(positionLiveList: Int)
    }

    companion object {
        const val LIVESTREAM_LAYOUT_MULTI = 1
        const val LIVESTREAM_LAYOUT_SINGLE = 0
    }
}