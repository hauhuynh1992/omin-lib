package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavouriteResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("checkStatus") val checkStatus: Boolean
) : Parcelable {

}