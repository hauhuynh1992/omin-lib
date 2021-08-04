package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QueryAssistant(
    @SerializedName("speech") val speech: String,
    @SerializedName("products") val products: ArrayList<Product>,
    @SerializedName("app_data") val appData: AppAssistantData,
    @SerializedName("context") val context: String? = null
) : Parcelable {
}
