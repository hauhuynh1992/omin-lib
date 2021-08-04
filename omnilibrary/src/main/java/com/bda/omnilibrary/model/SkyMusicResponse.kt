package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SkyMusicResponse(
    @SerializedName("status") var status: String = "",
    @SerializedName("data") var data: ArrayList<Data>,
    @SerializedName("totalRecords") var totalRecords: String = "",
    @SerializedName("loadMore") var loadMore: Boolean = false


) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("key") var key: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("artists") var artists: String = "",
        @SerializedName("duration") var duration: String = "",
        @SerializedName("kbit") var kbit: String = "",
        @SerializedName("dateModifire") var dateModifire: String = "",
        @SerializedName("streamUrl") var streamUrl: String = "",
        @SerializedName("songType") var songType: String = "",
        @SerializedName("relatedRight") var relatedRight: String = "",
        @SerializedName("copyRight") var copyRight: Boolean = false,
        @SerializedName("skyKey") var skyKey: String = ""

    ) : Parcelable {

    }
}