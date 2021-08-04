package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MomoPaymentResponce(

    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("success") val status: Boolean,
    @SerializedName("QRCode") val qrCode: String,
    @SerializedName("order_id") val order_id: String = "",
    @SerializedName("transaction_id") val transaction_id: String,
    @SerializedName("uid") val uid: String,
    @SerializedName("voucher_value") val voucher_value:Double,
    @SerializedName("message")val message:String,
    @SerializedName("has_transaction") val has_transaction: Boolean
) : Parcelable {
}