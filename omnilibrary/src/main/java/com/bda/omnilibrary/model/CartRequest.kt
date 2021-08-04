package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartRequest(
    @SerializedName("uid") val uid: String,
    @SerializedName("cart_items") val items: ArrayList<Product>
) : Parcelable {

}