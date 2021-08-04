package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoogleDeviceCodeRequest(
    @SerializedName("customer_id") var customer_id: String ,
    @SerializedName("popup_id")  var popup_id: String
) : Parcelable {

}