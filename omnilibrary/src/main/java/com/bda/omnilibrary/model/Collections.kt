package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

enum class CollectionType {
    HOT_DEAL,
    PRODUCT,
    NEW_ARRIVAL
}

@Parcelize
data class Collections(
    @SerializedName("uid") var uid: String,
    @SerializedName("collection_name") var collection_name: String = "",
    @SerializedName("highlight_name") var highlight_name: String = "",
    @SerializedName("products") private var _products: ArrayList<Product>,
    @SerializedName("count_items") var count_items: Int,
    @SerializedName("collection_image") var collection_image: String,
    @SerializedName("count_products") var count_products: Int,
    @SerializedName("count_children") var count_children: Int,
    @SerializedName("children") private var _children: ArrayList<Collections>,
    var type: CollectionType? = null

) : Parcelable {
    constructor() : this("", "", "", ArrayList(), 0, "", 0, 0, ArrayList())

    val products
        get() = (_products.takeIf { _products != null }
            ?: ArrayList())
    val children
        get() = (_children.takeIf { _children != null }
            ?: ArrayList())
}

@Parcelize
data class SimpleCollection(
    @SerializedName("uid") var uid: String,
    @SerializedName("collection_name") var collection_name: String = "",
    @SerializedName("collection_image") var collection_image: String = "",
    @SerializedName("collection_icon") var collection_icon: String = "",
    @SerializedName("color_title") var color_title: String="#000000",
    @SerializedName("gradient_start") var gradient_start: String="#000000",
    @SerializedName("gradient_end") var gradient_end: String="#000000"

) : Parcelable {
}