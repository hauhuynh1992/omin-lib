package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssistantAIResponse(
    @SerializedName("speechText") val speechText: String? = null,
    @SerializedName("speechBuffer") val speechBuffer: SpeechBuffer? = null,
    @SerializedName("title") val title: String,
    @SerializedName("suggestion") val suggestion: ArrayList<String>,
    @SerializedName("products") val products: ArrayList<Product>,
    @SerializedName("data") val data: String,
    @SerializedName("order_phone_number") val orderPhoneNumber: String? = null,
    @SerializedName("context") val context: String? = null,
) : Parcelable {

    @Parcelize
    data class SpeechBuffer(
        @SerializedName("type") val type: String? = null,
        @SerializedName("data") val data: ByteArray? = byteArrayOf()
    ) : Parcelable
}
