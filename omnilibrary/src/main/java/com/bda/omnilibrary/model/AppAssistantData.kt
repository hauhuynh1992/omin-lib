package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppAssistantData(
    @SerializedName("customer_id") val customerId: String? = null,
    @SerializedName("order_phone_number") val orderPhoneNumber: String? = null,
    @SerializedName("order_customer_name") val orderCustomerName: String? = null,
    @SerializedName("isFirstOpen") val isFirstOpen: Boolean = false,
    @SerializedName("langCode") val langCode: String? = null,
) : Parcelable {
}
