package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.productDetailActivity.ProductDetailActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.ImageUtils
//import com.github.islamkhsh.CardSliderAdapter
//import com.github.islamkhsh.CardSliderViewPager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.preview_image_item.view.*
import java.util.*


class ImageViewDialog(
    val activity: Activity,
    val list: ArrayList<Product.MediaType>,
    var position: Int,
) {

    private var dialog: AlertDialog? = null
    private var mActivity = activity
//    private var back: ImageView
//    private var forward: ImageView

//    private var viewPager: CardSliderViewPager? = null
//    private var mView =
//        activity.layoutInflater.inflate(R.layout.image_layout_dialog, null) as ViewGroup
//
//    init {
//        viewPager = mView.findViewById(R.id.viewPagerImage)
//        back = mView.findViewById(R.id.ic_back)
//        forward = mView.findViewById(R.id.ic_forward)
//
//        dialog = AlertDialog.Builder(activity)
//            .setOnCancelListener { }
//            .create().apply {
//                setView(mView)
//                setCanceledOnTouchOutside(false)
//                show()
//                window?.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//            }
//        dialog?.window?.setDimAmount(0f)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
//        dialog?.setOnKeyListener { _, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN) {
//                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    if (viewPager!!.currentItem < list.size) {
//
//                        val dataObject = LogDataRequest()
//                        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
//                        dataObject.attachmentType = list[viewPager!!.currentItem].mediaType
//                        dataObject.attachmentUrl = list[viewPager!!.currentItem].url
//                        val data = Gson().toJson(dataObject).toString()
//                        (mActivity as ProductDetailActivity).logTrackingVersion2(
//                            QuickstartPreferences.SLIDE_NEXT_ATTACHMENT_v2,
//                            data
//                        )
//                        viewPager!!.currentItem = viewPager!!.currentItem + 1
//                        handleArrow(viewPager!!.currentItem)
//                    }
//                }
//                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//                    if (viewPager!!.currentItem > 0) {
//                        val dataObject = LogDataRequest()
//                        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
//                        dataObject.attachmentType = list[viewPager!!.currentItem].mediaType
//                        dataObject.attachmentUrl = list[viewPager!!.currentItem].url
//                        val data = Gson().toJson(dataObject).toString()
//                        (mActivity as ProductDetailActivity).logTrackingVersion2(
//                            QuickstartPreferences.SLIDE_PREVIOUS_ATTACHMENT_v2,
//                            data
//                        )
//                        viewPager!!.currentItem = viewPager!!.currentItem - 1
//                        handleArrow(viewPager!!.currentItem)
//                    }
//                }
//            }
//            false
//        }
//        viewPager!!.adapter = ImageAdapter(list, mActivity)
//        viewPager!!.currentItem = position
//        handleArrow(viewPager!!.currentItem)
//    }
//
//    private fun handleArrow(position: Int) {
//        back.setImageResource(R.mipmap.ic_baseline_arrow_back)
//        forward.setImageResource(R.mipmap.ic_baseline_arrow_forward)
//
//
//        when (position) {
//            0 -> {
//                back.setImageResource(R.mipmap.ic_baseline_arrow_back_disable)
//            }
//
//            list.size - 1 -> {
//                forward.setImageResource(R.mipmap.ic_baseline_arrow_forward_disable)
//            }
//        }
//    }
//
//    fun dismiss() {
//        dialog?.dismiss()
//    }
//
//    class ImageAdapter(private val movies: ArrayList<Product.MediaType>, activity: Activity) :
//        CardSliderAdapter<ImageAdapter.ImageViewHolder>() {
//        private var mActivity = activity
//        override fun getItemCount() = movies.size
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
//            val view = LayoutInflater.from(mActivity)
//                .inflate(R.layout.preview_image_item, parent, false)
//            return ImageViewHolder(view)
//        }
//
//        override fun bindVH(holder: ImageViewHolder, position: Int) {
//            ImageUtils.loadImage(
//                mActivity,
//                holder.image_preview_image,
//                movies[position].icon,
//                ImageUtils.TYPE_SHOW.takeIf { movies[position].square }
//                    ?: ImageUtils.TYPE_PRIVIEW_LAGE
//            )
//        }
//
//        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var image_preview_image: ImageView = view.image_preview_large
//        }
//    }
}


