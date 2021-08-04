package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterOtpRequest(
    @SerializedName("otp_code") var otpCode: String,
    @SerializedName("phone_number") var phoneNumber: String
) : Parcelable