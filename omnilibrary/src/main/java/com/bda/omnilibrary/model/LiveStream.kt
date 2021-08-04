package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveStream(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("stream.name") var name: String = "",
    @SerializedName("stream.display_name") var display_name: String = "",
    @SerializedName("stream.start_time") private var start_time: Double,
    @SerializedName("stream.end_time") var end_time: Double,
    @SerializedName("stream.type") var type: String = "",
    @SerializedName("stream.video") var video: String = "",
    @SerializedName("stream.image_thumb") var image_thumb: String = "",
    @SerializedName("stream.image_fullscreen") var image_fullscreen: String = "",
    @SerializedName("stream.view_count") var view_count: Int,
    @SerializedName("stream.view_virtual") var view_virtual: Int,
    @SerializedName("stream.is_highlight") var is_highlight: Boolean,
    @SerializedName("display_status") var display_status: Int,
    @SerializedName("stream.channel") private var _channel: ArrayList<Channel>,
    @SerializedName("stream.status") var status: Int = 0, /// 0 chưa live, 1 đang live,2 đã live
    @SerializedName("stream.products") private var _products: ArrayList<StreamProduct>,
    @SerializedName("stream.video_transcode") var video_transcode: String = "",
    @SerializedName("is_portrait") var is_portrait: Boolean = false
) : Parcelable {
    constructor() : this(
        "", "", "", 0.0, 0.0, "",
        "", "", "", 0, 0, false,
        0, arrayListOf(), 0, arrayListOf(), ""
    )

    val products
        get() = _products.takeIf { _products != null }
            ?: ArrayList()

    val channel
        get() = _channel.takeIf { _channel != null }
            ?: ArrayList()

    @Parcelize
    data class Channel(
        @SerializedName("uid") var _uid: String = "",
        @SerializedName("channel.name") var name: String = "",
        @SerializedName("channel.link") var link: String = ""
    ) : Parcelable {
    }

    @Parcelize
    data class StreamProduct(
        @SerializedName("uid") var _uid: String = "",
        @SerializedName("stream_product.product") var product: Product = Product(false),
        @SerializedName("time") var time: Int = -1,
        @SerializedName("isHighlight") var isHighlight: Boolean = false

    ) : Parcelable {
    }
}