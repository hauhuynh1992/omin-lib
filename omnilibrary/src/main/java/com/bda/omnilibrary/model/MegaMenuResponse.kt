package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MegaMenuResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("megamenu") var megamenu: MegaMenu
) : Parcelable {
    constructor() : this(
        -1, MegaMenu(
            0, 0,
            MegaMenu.MegaMenuCollection("", "")
        )
    )

    @Parcelize
    data class MegaMenu(
        @SerializedName("order_total") var order_total: Int,
        @SerializedName("date_total_order") var date_total_order: Int,
        @SerializedName("order_collection") var order_collection: MegaMenuCollection
    ) : Parcelable {
        @Parcelize
        data class MegaMenuCollection(
            @SerializedName("uid") var uid: String,
            @SerializedName("collection_name") var collection_name: String
        ) : Parcelable {
        }
    }
}