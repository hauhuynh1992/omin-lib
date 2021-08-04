package com.bda.omnilibrary.util

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ImageView
import com.bda.omnilibrary.views.SfTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition


object ImageUtils {
    val TYPE_PRODUCT = 0
    val TYPE_HOTDEAL = 1
    val TYPE_COLLECTION = 2
    val TYPE_PRIVIEW_SMALL = 3
    val TYPE_PRIVIEW_LAGE = 4
    val TYPE_CART = 5
    val TYPE_SHOW = 6
    val TYPE_VIDEO = 7
    val TYPE_PRIVIEW_BRAND_SHOP_LAGE = 9
    val TYPE_BRAND_SHOP_AVATAR = 10
    val TYPE_BRAND_SHOP_IMAGE = 11
    val TYPE_HYBRID = 12
    val TYPE_LANDING = 13
    val TYPE_VIDEO_SINGLE = 14

    var localUrl: String? = null
    var videoUrl: String? = null

    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
        .skipMemoryCache(true)

    private fun formatLink(link: String?, tagetWidth: Int, tagetHeight: Int): String {
        var width = tagetWidth
        width = width * 80 / 100
        var height = tagetHeight
        height = height * 80 / 100
        return if (link != null && link.contains("fptshop")) {
            "https://images.fpt.shop/unsafe/fit-in/" + width + "x" + height + "/filters:quality(90):fill(white)/$link"
        } else if (link != null && link.contains("launcher")) {
            "$link?mode=scale&w=$width&h=$height&fmt=webp"
        } else {
            "$localUrl/$link?mode=scale&w=$width&h=$height&fmt=webp"
        }
    }

    fun loadImage(activity: Activity, image: ImageView, imageLink: String?, type: Int) {
        if (isValidContextForGlide(activity)) {

            if (localUrl != null && imageLink != null && imageLink.isNotEmpty()) {
                Glide.with(activity)
                    .load(formatLink(imageLink, tagetWidth(type), tagetHeight(type)))
                    // .apply(requestOption
                    .timeout(30000)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }


                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            target!!.onResourceReady(
                                resource!!,
                                DrawableCrossFadeTransition(1000, !isFirstResource)
                            )
                            return true
                        }

                    })
                    .into(image)
            }
        }
    }

    fun loadImageHybrid(activity: Activity, image: ImageView, imageLink: String?, type: Int) {
        if (isValidContextForGlide(activity)) {
            if (localUrl != null && imageLink != null && imageLink.isNotEmpty()) {

                Glide.with(activity)
                    .load(formatLink(imageLink, tagetWidth(type), tagetHeight(type)))
                    // .apply(requestOptions)
                    .placeholder(android.R.color.transparent)
                    .timeout(30000)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            target!!.onResourceReady(
                                resource!!,
                                DrawableCrossFadeTransition(1000, !isFirstResource)
                            )
                            return true
                        }

                    })
                    .into(image)
            }
        }
    }

    fun loadImageCache(activity: Activity, image: ImageView, imageLink: String?, type: Int) {
        if (isValidContextForGlide(activity)) {
            if (localUrl != null && imageLink != null && imageLink.isNotEmpty()) {
                Glide.with(activity)

                    .load(formatLink(imageLink, tagetWidth(type), tagetHeight(type)))
                    .timeout(30000)
                    .placeholder(android.R.color.transparent)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?,
                        ) {
                            image.setImageDrawable(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }
        }
    }

    fun loadImage(activity: Activity, image: ImageView, imageLink: String?) {
        if (isValidContextForGlide(activity)) {
            if (localUrl != null && imageLink != null && imageLink.isNotEmpty()) {
                Glide.with(activity)
                    .load("$localUrl/$imageLink")
                    //.apply(requestOptions)
                    .centerInside()
                    .timeout(30000)
                    .placeholder(android.R.color.transparent)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?,
                        ) {
                            image.setImageDrawable(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }
        }
    }

    fun loadImageNoCache(activity: Activity, image: ImageView, imageLink: String?) {
        if (isValidContextForGlide(activity)) {
            if (imageLink != null) {
                Glide.with(activity)
                    .load(imageLink)
                    .apply(requestOptions)
                    .timeout(30000)
                    .placeholder(android.R.color.transparent)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?,
                        ) {
                            image.setImageDrawable(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }
        }
    }

    fun loadImageTextView(activity: Activity, txtView: SfTextView, imageLink: String?) {
        if (isValidContextForGlide(activity)) {
            if (localUrl != null && imageLink != null && imageLink.isNotEmpty()) {
                Glide.with(activity)
                    .load("$localUrl/$imageLink")
                    //.apply(requestOptions)
                    .centerInside()
                    .placeholder(android.R.color.transparent)
                    .timeout(30000)
                    .into(object : CustomTarget<Drawable?>() {

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?,
                        ) {
                            txtView.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }
        }
    }

    fun loadVideo(url: String): String {
        return "$localUrl/$url"
    }

    fun loadQrCodeFromBase64(encode: String, view: ImageView) {
        // split header "data:image/png;base64"
        val pureBase64Encoded = encode.substring(encode.indexOf(",") + 1)
        // decode
        val decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        // load image
        view.setImageBitmap(decodedByte)
    }

    fun tagetWidth(type: Int): Int {
        when (type) {
            TYPE_PRODUCT -> return 600
            TYPE_HOTDEAL -> return 800
            TYPE_COLLECTION -> return 800
            TYPE_PRIVIEW_SMALL -> return 200
            TYPE_PRIVIEW_LAGE -> return 1920
            TYPE_CART -> return 200
            TYPE_SHOW -> return 1000
            TYPE_VIDEO -> return 800
            TYPE_PRIVIEW_BRAND_SHOP_LAGE -> return 1920
            TYPE_BRAND_SHOP_AVATAR -> return 300
            TYPE_BRAND_SHOP_IMAGE -> return 770
            TYPE_HYBRID -> return 1740
            TYPE_LANDING -> return 1920
            TYPE_VIDEO_SINGLE -> return 1600
        }
        return 400
    }

    fun tagetHeight(type: Int): Int {
        when (type) {
            TYPE_PRODUCT -> return 600
            TYPE_HOTDEAL -> return 450
            TYPE_COLLECTION -> return 400
            TYPE_PRIVIEW_SMALL -> return 200
            TYPE_PRIVIEW_LAGE -> return 1080
            TYPE_CART -> return 200
            TYPE_SHOW -> return 1000
            TYPE_VIDEO -> return 450

            TYPE_PRIVIEW_BRAND_SHOP_LAGE -> return 460
            TYPE_BRAND_SHOP_AVATAR -> return 300
            TYPE_BRAND_SHOP_IMAGE -> return 330
            TYPE_HYBRID -> return 544
            TYPE_LANDING -> return 980
            TYPE_VIDEO_SINGLE -> return 900
        }
        return 400
    }

    private fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            if (context.isDestroyed || context.isFinishing) {
                return false
            }
        }
        return true
    }

}