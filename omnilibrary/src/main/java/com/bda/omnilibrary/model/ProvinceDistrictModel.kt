package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProvinceDistrictModel(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("data") val data: ArrayList<Data>
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("uid") val uid: String,
        @SerializedName("name") val name: String
    ) : Parcelable {

    }
}