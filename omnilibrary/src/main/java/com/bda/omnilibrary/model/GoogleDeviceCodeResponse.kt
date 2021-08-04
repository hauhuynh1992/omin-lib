package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoogleDeviceCodeResponse(
    @SerializedName("device_code") var deviceCode: String,
    @SerializedName("user_code") var userCode: String,
    @SerializedName("expires_in") var expiresIn: Int,
    @SerializedName("interval") var interval: Int,
    @SerializedName("verification_url") var verificationUrl: String,
) : Parcelable