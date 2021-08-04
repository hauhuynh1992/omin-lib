package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateCustomerRequest(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("phone_number") var phone: String = "",
    @SerializedName("customer_name") var name: String = "",
    @SerializedName("address") var address: Address = Address(),
    @SerializedName("alt_info") var alt_info: ArrayList<ContactInfo>

) : Parcelable {
}