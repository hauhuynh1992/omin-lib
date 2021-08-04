package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ViewCountRequest(
    @SerializedName("uid") var uid: String,
    @SerializedName("inc") var inc: Int

) : Parcelable {
}