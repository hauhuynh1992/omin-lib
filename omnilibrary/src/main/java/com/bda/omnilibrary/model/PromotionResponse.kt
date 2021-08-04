package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromotionResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("message") var message: String = "",
    @SerializedName("data") var data: ArrayList<Promotion>
) : Parcelable {
}