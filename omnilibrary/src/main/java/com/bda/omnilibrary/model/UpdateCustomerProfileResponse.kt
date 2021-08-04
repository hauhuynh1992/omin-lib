package com.bda.omnilibrary.model

import com.google.gson.annotations.SerializedName

data class UpdateCustomerProfileResponse(
    @SerializedName("statusCode") val statusCode: Int
)