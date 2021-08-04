package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavouriteResquest(
    @SerializedName("customer_id") val customer_id: String,
    @SerializedName("product_uid") val product_uid: String
) : Parcelable {

}