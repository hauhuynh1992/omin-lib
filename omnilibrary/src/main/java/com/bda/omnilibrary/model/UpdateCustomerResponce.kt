package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateCustomerResponce(
    @SerializedName("statusCode") var status: Int
) : Parcelable {
}