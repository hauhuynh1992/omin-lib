package com.bda.omnilibrary.model

import com.google.gson.annotations.SerializedName

data class LogResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String
    )