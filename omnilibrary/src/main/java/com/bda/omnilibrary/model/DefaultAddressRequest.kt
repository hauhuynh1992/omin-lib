package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DefaultAddressRequest(
    @SerializedName("customer_id") var customer_id: String,
    @SerializedName("address_id") var address_id: String
) : Parcelable {

}