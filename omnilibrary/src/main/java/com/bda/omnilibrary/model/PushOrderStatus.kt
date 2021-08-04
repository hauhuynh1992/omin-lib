package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PushOrderStatus(
    @SerializedName("order_id") val uid: String,
    @SerializedName("order_status") val status: Int,
    @SerializedName("reason") val reason: String
) : Parcelable {
}
