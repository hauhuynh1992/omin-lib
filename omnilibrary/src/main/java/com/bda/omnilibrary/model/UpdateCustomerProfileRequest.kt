package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateCustomerProfileRequest(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("profile_phone_number") var phone: String = "",
    @SerializedName("customer_name") var name: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("gender") var gender: Int = 0,
    @SerializedName("dateOfBirth") var dateOfBirth: Long = 0,
    @SerializedName("address") var address: Address? = null

) : Parcelable