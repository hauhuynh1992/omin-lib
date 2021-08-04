package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavouriteProductResponse(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("result") val data: ArrayList<Product>
) : Parcelable {
}