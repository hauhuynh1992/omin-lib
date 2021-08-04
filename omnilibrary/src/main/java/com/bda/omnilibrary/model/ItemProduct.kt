package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemProduct(
    @SerializedName("item_name") val itemName: String? = null,
    @SerializedName("item_id") val itemId:  String? = null,
    @SerializedName("item_price") val price: String? = null,
    @SerializedName("item_no") val itemNo: String? = null,
    @SerializedName("item_quantity") val itemQuantity: String? = null,
) : Parcelable {


    override fun toString(): String {
        return "Product(itemName: " + itemName + "," +
                "itemId: " + itemId + "," +
                "itemQuantity: " + itemQuantity + "," +
                "itemNo: " + itemNo + "," +
                "itemPrice: " + price + ")"
    }
}
