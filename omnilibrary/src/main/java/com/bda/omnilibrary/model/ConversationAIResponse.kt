package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConversationAIResponse(
    @SerializedName("status") val status: Status,
    @SerializedName("data") val data: Data,
    @SerializedName("history_id") val history_id: String
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("intents") val intents: ArrayList<Intent>?,
        @SerializedName("entities") val entities: ArrayList<Entity>??
    ) : Parcelable {

        @Parcelize
        data class Entity(
            @SerializedName("start") val start: Int,
            @SerializedName("end") val end: Int,
            @SerializedName("value") val value: String,
            @SerializedName("real_value") val real_value: String,
            @SerializedName("entity") val entity: String

        ) : Parcelable

        @Parcelize
        data class Intent(
            @SerializedName("label") val label: String,
            @SerializedName("confidence") val confidence: Float
        ) : Parcelable
    }

    @Parcelize
    data class Status(
        @SerializedName("code") val code: Int,
        @SerializedName("message") val message: String,
        @SerializedName("module") val module: String,
        @SerializedName("api_code") val api_code: Int,
        @SerializedName("err_code") val err_code: Int,
        @SerializedName("detail") val detail: String,
        @SerializedName("app_code") val app_code: String
    ) : Parcelable

    override fun toString(): String {
        return "ConversationAIResponse(status=$status, data=$data, history_id='$history_id')"
    }


}
