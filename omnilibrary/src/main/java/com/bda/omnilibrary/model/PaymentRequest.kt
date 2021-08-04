package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentRequest(
    @SerializedName("pay_gateway") val payType: String,
    @SerializedName("customer_id") val cid: String,
    @SerializedName("phone_number") val phone: String,
    @SerializedName("customer_name") val name: String,
    @SerializedName("voucher_code") val voucher_code: String,
    @SerializedName("voucher_uid") val voucher_uid: String,
    @SerializedName("address_des") val address: String,
    @SerializedName("province") val province: String,
    @SerializedName("district") val district: String,
    @SerializedName("address_type") val addressType: Int = 1,
    @SerializedName("items") val items: ArrayList<Item>,
    @SerializedName("platform") val platform: String,
    @SerializedName("request_delivery_time") val requestDeliveryTime: String,
    @SerializedName("client_notes") val note: String,
    @SerializedName("created_address_des") val created_address_des: String,
    @SerializedName("created_customer_name") val created_customer_name: String,
    @SerializedName("created_phone_number") val created_phone_number: String,
    @SerializedName("created_province") val created_province: String,
    @SerializedName("created_district") val created_district: String,
    @SerializedName("create_address_type") val create_address_type: Int


    ) : Parcelable {

    @Parcelize
    data class Item(
        @SerializedName("uid") val uid: String,
        @SerializedName("quantity") val quantity: Int
    ) : Parcelable {
    }
}