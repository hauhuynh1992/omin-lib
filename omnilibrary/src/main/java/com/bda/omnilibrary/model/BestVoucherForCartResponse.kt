package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BestVoucherForCartResponse(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("data") val data: Voucher? = null,
    @SerializedName("message") val message: String
) : Parcelable {
}