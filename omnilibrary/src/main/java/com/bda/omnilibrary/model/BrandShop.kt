package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BrandShop(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("display_name") private var _display_name: String,
    @SerializedName("brand_shop_name") private var brand_shop_name: String,
    @SerializedName("display_name_detail") private var _display_name_detail: String,
    @SerializedName("hotline") var hotline: String,
    @SerializedName("certification_tag") var certification_tag: Boolean = false,
    @SerializedName("image_banner") var image_banner: String = "",
    @SerializedName("image_logo") var image_logo: String = "",
    @SerializedName("image_details") private var _image_details: ArrayList<MediaType>,
    @SerializedName("display_status") var display_status: Int = 0,
    @SerializedName("product_counts") var product_counts: Int = 0,
    @SerializedName("description_html") var description_html: String,
    @SerializedName("brand_shop_description") private var brand_shop_description: String,
    @SerializedName("display_name_in_product") private var _display_name_in_product: String,
    @SerializedName("skin_name")  var skin_name: String? = null,
    @SerializedName("skin_image") var skin_image: String? = null,
    @SerializedName("skin_display_at_home")  var skin_display_at_home: Boolean = false,
    @SerializedName("skin_display_at_brandshop")  var skin_display_at_brandshop: Boolean = false,
) : Parcelable {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        false,
        "",
        "",
        ArrayList<MediaType>(),
        0,
        0, "",
        "", ""
    )

    val name
        get() = (_display_name.takeIf { _display_name != null && _display_name.isNotEmpty() }
            ?: "").trim()

    val display_name_detail
        get() = (_display_name_detail.takeIf { _display_name_detail != null && _display_name_detail.isNotEmpty() }
            ?: "").trim()

    val display_name_in_product
        get() = (_display_name_in_product.takeIf { _display_name_in_product != null && _display_name_in_product.isNotEmpty() }
            ?: display_name_detail).trim()


    val images
        get() = (_image_details.takeIf { _image_details != null }
            ?: ArrayList())

    val introButtonText
        get() = (brand_shop_description.takeIf { brand_shop_description != null && brand_shop_description != "" }
            ?: "GIỚI THIỆU CỬA HÀNG")

    @Parcelize
    data class MediaType(
        @SerializedName("media_type") var mediaType: String = "",
        @SerializedName("thumb") var icon: String = "",
        @SerializedName("source") var url: String = "",
        @SerializedName("square") var square: Boolean,

        ) : Parcelable {}
}