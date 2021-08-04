package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductCollection(
    @SerializedName("uid") var uid: String,
    @SerializedName("layout_type") var layout_type: String,

    @SerializedName("collection_name") var collection_name: String = "",
    @SerializedName("items") private var _items: ArrayList<Product>

) : Parcelable {
    constructor() : this("", "","", arrayListOf())

    val items
        get() = _items.takeIf { _items != null }
            ?: ArrayList()
}