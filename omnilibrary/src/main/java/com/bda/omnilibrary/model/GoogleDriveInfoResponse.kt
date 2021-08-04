package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoogleDriveInfoResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("user") var user: GoogleDriveUser,
) : Parcelable {

    @Parcelize
    data class GoogleDriveUser(
        @SerializedName("displayName") val displayName: String,
        @SerializedName("permissionId") val permissionId: String,
        @SerializedName("emailAddress") val emailAddress: String,
    ) : Parcelable
}