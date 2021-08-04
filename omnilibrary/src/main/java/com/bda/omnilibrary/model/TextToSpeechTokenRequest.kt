package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextToSpeechTokenRequest(
    @SerializedName("access_token") var access_token: String
) : Parcelable {

}