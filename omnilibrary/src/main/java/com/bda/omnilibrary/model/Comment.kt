package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    @SerializedName("name") var name: String = "",
    @SerializedName("isChecked") var isChecked: Boolean = false

) : Parcelable {


}