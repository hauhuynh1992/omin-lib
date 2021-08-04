package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DefaultAddressResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("message") var message: String = ""
) : Parcelable {

}