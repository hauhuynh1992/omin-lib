package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubCollection(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("collectionName") var collectionName: String,
    @SerializedName("data") var data: ArrayList<Collections>,
@SerializedName("Hotdeals") private var _hotdeal: Collections
) : Parcelable {
    val hotdeal
    get() = (_hotdeal.takeIf { _hotdeal != null }
        ?: Collections())
}