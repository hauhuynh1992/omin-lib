package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FPTAIModel(
    @SerializedName("async") val async: String,
    @SerializedName("error") val error: Int,
    @SerializedName("message") val message: String,
    @SerializedName("request_id") val request_id: String
) : Parcelable {

}