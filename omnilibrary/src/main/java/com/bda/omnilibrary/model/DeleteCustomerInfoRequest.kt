package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeleteCustomerInfoRequest(
    @SerializedName("cus_uid") val uid: String = "",
    @SerializedName("info_uid") val infoUid: String = ""
) : Parcelable {}