package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Voucher(
    @SerializedName("uid") var uid: String,
    @SerializedName("voucher_code") var voucher_code: String,
    @SerializedName("voucher_label") var voucher_label: String,
    @SerializedName("voucher_type") var voucher_type: Int,
    @SerializedName("reference_type") var reference_type: Int,
    @SerializedName("target_type") var target_type: Int,
    @SerializedName("condition_type") var condition_type: Int,
    @SerializedName("condition_value") var condition_value: Long,
    @SerializedName("voucher_value") var voucher_value: Double = 0.0,
    @SerializedName("redeem") var redeem: Int,
    @SerializedName("start_at") var start_at: Long,
    @SerializedName("stop_at") var stop_at: Long,
    @SerializedName("max_applied_value") var max_applied_value: Long,
    @SerializedName("collection_image") var collection_image: String,
    @SerializedName("image_highlight") var image_highlight: String,
    @SerializedName("voucher_type_label") var voucher_type_label: String,
    @SerializedName("reference_type_label") var reference_type_label: String,
    @SerializedName("condition_type_label") var condition_type_label: String,
    @SerializedName("products") private var _products: ArrayList<Product>,
    @SerializedName("collections") private var _collections: ArrayList<Collections>,
    @SerializedName("applied_value") var applied_value: Double,
    @SerializedName("highlight_products") var highlight_products: ArrayList<HightlightProduct>

) : Parcelable {
    constructor() : this(
        "", "", "", -1, -1,
        -1, -1, 1, 0.0, 0, 0, 0, 0,"","","",
        "", "", ArrayList(), ArrayList(), 0.0, arrayListOf()
    )

    constructor(uid: String, voucher_code: String) : this(
        uid, voucher_code, "", -1, -1,
        -1, -1, 1, 0.0, 0, 0, 0, 0,"","","",
        "", "", ArrayList(), ArrayList(), 0.0, arrayListOf()
    )

    val products
        get() = (_products.takeIf { _products != null }
            ?: ArrayList())
    val collections
        get() = (_collections.takeIf { _collections != null }
            ?: ArrayList())

    @Parcelize
    data class HightlightProduct(
        @SerializedName("cost_price") var cost_price: Double,
        @SerializedName("partner_id") var partner_id: String,
        @SerializedName("price_with_vat") var price_with_vat: Double,
        @SerializedName("product_name") var product_name: String,
        @SerializedName("uid") var uid: String

    ) : Parcelable {
        constructor() : this(0.0, "", 0.0, "", "")
    }
}