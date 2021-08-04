package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Predict(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("commands") val commands: ArrayList<Command>?,
        @SerializedName("responseText") val responseText: String?
    ) : Parcelable {

        @Parcelize
        data class Command(
            @SerializedName("command") val command: String?,
            @SerializedName("params") val params: ArrayList<Product>?,
            @SerializedName("suggestions") val suggestions: ArrayList<String>?
        ) : Parcelable {

        }
    }

    override fun toString(): String {
        return "AssistantAIResponse(code=$code, message='$message', data=$data)"
    }
}
