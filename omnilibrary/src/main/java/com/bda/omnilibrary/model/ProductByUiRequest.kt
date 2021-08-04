package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductByUiRequest(
    @SerializedName("uids") val uids: ArrayList<String>
) : Parcelable {
}

