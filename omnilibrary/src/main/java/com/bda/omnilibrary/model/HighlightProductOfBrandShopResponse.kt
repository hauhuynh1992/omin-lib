package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HighlightProductOfBrandShopResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("data") val data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("uid") val uid: String = "",
        @SerializedName("highlight_name") val highlight_name: String = "",
        @SerializedName("products") val products: ArrayList<Product>
    ) : Parcelable {
    }
}