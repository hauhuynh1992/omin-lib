package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfigModel(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("config") val config: Config
) : Parcelable {

    @Parcelize
    data class Config(
        @SerializedName("env") val env: String,
        @SerializedName("static_url") val url: String,
        @SerializedName("fptplay_api") val urlFpt: String,
        @SerializedName("isFreeShipping") val isFreeShipping: Boolean = false,
        @SerializedName("offsetCount") val offsetCount: Int = 0,
        @SerializedName("video_url") val video_url: String,
        @SerializedName("image_url") val image_url: String

    ) : Parcelable
}