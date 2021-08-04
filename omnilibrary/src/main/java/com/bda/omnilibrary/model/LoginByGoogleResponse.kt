package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginByGoogleResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("customer") var customer: UserGoogleInfo
) : Parcelable {
    @Parcelize
    data class UserGoogleInfo(
        @SerializedName("customer_name") val customerName: String,
        @SerializedName("email") val email: String,
        @SerializedName("google_id") val googleId: String,
        @SerializedName("uid") val uid: String,
    ) : Parcelable
}
