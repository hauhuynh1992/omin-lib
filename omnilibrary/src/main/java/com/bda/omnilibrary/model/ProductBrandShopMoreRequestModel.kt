package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductBrandShopMoreRequestModel(
    @SerializedName("collection_uid") val collection_uid: String,
    @SerializedName("brandshop_uid") val brandshop_uid: String,
    @SerializedName("length") val length: Int,
    @SerializedName("page") val page: Int
) : Parcelable {

}