package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HightlightModel(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("data") private val _products: ArrayList<Product>
) : Parcelable {
    val products
        get() = _products.takeIf { _products != null } ?: ArrayList()
}