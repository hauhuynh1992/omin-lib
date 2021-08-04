package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BrandShopResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("data") val brandShop: BrandShop
    ) : Parcelable {
}