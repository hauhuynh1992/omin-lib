package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactInfo(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("customer_name") var customer_name: String = "",
    @SerializedName("phone_number") var phone_number: String = "",
    @SerializedName("is_default_address") var is_default_address: Boolean = false,
    @SerializedName("address") var address: Address = Address()

) : Parcelable {

}