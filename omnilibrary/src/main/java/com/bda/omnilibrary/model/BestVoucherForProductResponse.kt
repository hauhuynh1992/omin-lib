package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BestVoucherForProductResponse(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("data") val data: Voucher
) : Parcelable {
}