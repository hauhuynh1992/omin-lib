package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

enum class AddressType (val type: Int){
    HOME(1),
    APARTMENT(2)
}

@Parcelize
data class Location(
    @SerializedName("latitude") val mLatitude: Double,
    @SerializedName("longitude") val mLongitude: Double,
    @SerializedName("address") val address: String
): Parcelable {
}