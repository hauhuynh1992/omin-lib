package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchProductResponse(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("data") private var _data: ArrayList<Product>,
    @SerializedName("brand_Shop") private var _brand_Shop: ArrayList<BrandShop>
) : Parcelable {
    val data
        get() = (_data.takeIf { _data != null }
            ?: arrayListOf())

    val brand_Shop
        get() = (_brand_Shop.takeIf { _brand_Shop != null }
            ?: arrayListOf())
}