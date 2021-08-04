package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QueryFPTConversation(
    @SerializedName("content") val content: String,
    @SerializedName("save_history") val save_history: Boolean
) : Parcelable {
}
