package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PopUpRequest(
    @SerializedName("customer_id") var customer_id: String,
    @SerializedName("flatform_id") private var flatform_id: String,
    @SerializedName("screen_id") private var screen_id: String,
    @SerializedName("brandshop_id") private var brandshop_id: String = ""
) : Parcelable {

}