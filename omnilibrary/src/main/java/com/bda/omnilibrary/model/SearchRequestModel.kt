package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchRequestModel(
    @SerializedName("keywords") val keyworks: String

) : Parcelable {
}