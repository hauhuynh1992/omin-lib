package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FPTAITextToSpeedModel(
    @SerializedName("async") val async: String,
    @SerializedName("error") val error: Int,
    @SerializedName("message") val message: String,
    @SerializedName("request_id") val request_id: String
) : Parcelable {
    override fun toString(): String {
        return "FPTAIModel(async='$async', error=$error, message='$message', request_id='$request_id')"
    }

}
