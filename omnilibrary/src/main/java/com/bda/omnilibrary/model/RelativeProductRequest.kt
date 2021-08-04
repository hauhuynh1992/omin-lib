package com.bda.omnilibrary.model

import com.google.gson.annotations.SerializedName

data class RelativeProductRequest(
    @SerializedName("uid") val uid: String
)