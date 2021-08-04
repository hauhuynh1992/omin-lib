package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class HistoryModel(
    @SerializedName("uids") val uids: ArrayList<String>,
    @SerializedName("customer_id") var customer_id: String
) : Parcelable {
}
