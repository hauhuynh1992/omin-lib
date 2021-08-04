package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LandingPageModel(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("data") var data: Data?,
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("landing_name") var landing_name: String?,
        @SerializedName("image_cover") private var _image_cover: String?
    ) : Parcelable {
        val image_cover
            get() = _image_cover.takeIf { _image_cover != null }
                ?: ""
    }

}