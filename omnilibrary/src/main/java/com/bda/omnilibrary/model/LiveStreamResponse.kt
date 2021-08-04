package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveStreamResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("data") private var _liveStreams: LiveStream
) : Parcelable {
    val liveStreams
        get() = _liveStreams.takeIf { _liveStreams != null }
            ?: LiveStream()
}