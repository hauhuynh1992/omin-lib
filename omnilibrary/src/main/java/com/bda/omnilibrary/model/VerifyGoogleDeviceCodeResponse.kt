package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerifyGoogleDeviceCodeResponse(
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("expires_in") var expiresIn: Int,
    @SerializedName("refresh_token") var refreshToken: String,
    @SerializedName("scope") var scope: String,
    @SerializedName("token_type") var token_type: String
) : Parcelable