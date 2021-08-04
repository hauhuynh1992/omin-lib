package com.bda.omnilibrary.model

import android.os.Parcelable
import android.speech.tts.Voice
import com.bda.omnilibrary.util.Config
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TTSRequest(
    @SerializedName("audioConfig") var audioConfig: AudioConfig,
    @SerializedName("input") var input: Input,
    @SerializedName("voice") var voice: Voice
) : Parcelable {

    @Parcelize
    data class AudioConfig(
        @SerializedName("audioEncoding") var audioEncoding: String? = "MP3",
        @SerializedName("pitch") var pitch: Int = 0,
        @SerializedName("speakingRate") var speakingRate: Int = 1

    ) : Parcelable {}

    @Parcelize
    data class Input(
        @SerializedName("text") var text: String

    ) : Parcelable {}

    @Parcelize
    data class Voice(
        @SerializedName("languageCode") var languageCode: String = "vi-VN",
        @SerializedName("name") var name: String = Config.ASSISTANT_VOICE.NGOC_CHAU.voice,
    ) : Parcelable {}
}