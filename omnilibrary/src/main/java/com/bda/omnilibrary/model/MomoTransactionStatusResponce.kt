package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MomoTransactionStatusResponce (
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("order_id") val oId: String,
    @SerializedName("status") val transactionStatus: String
): Parcelable {
}