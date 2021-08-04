package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MusicResponse(
    @SerializedName("result") var result: ArrayList<Music>

) : Parcelable {
    @Parcelize
    data class Music(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("song_id") var song_id: String = "",
        @SerializedName("song_name") var song_name: String = "",
        @SerializedName("singer_name") var singer_name: String = ""
    ) : Parcelable {

    }
}