package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Region(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("vn_all_province") var vn_all_province: Boolean = false

) : Parcelable {


}