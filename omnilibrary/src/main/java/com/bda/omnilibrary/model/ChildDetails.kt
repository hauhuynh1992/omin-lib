package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
    data class ChildDetails(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("data") var data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("uid") var uid: String,
        @SerializedName("collection_name") var collection_name: String,
        @SerializedName("count_items") var count_items: Int,
        @SerializedName("products") private var _products: ArrayList<Product>

    ) : Parcelable {
        val products
            get() = (_products.takeIf { _products != null }
                ?: ArrayList())
    }
}