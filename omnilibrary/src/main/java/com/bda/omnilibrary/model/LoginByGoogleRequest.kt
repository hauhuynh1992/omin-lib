package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginByGoogleRequest(
    @SerializedName("email") var email: String,
    @SerializedName("google_token") var googleToken: String,
    @SerializedName("customer_name") var customerName: String,
    @SerializedName("google_id") var googleId: String,
) : Parcelable