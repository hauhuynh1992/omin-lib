package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListOrderResponceV3(
    @SerializedName("statusCode") val status: Int,
    @SerializedName("data") val data: ArrayList<Data>

) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("order_id") val uid: String,
        @SerializedName("pay_status") val payStatus: Int,
        @SerializedName("order_status") val orderStatus: Int,
        @SerializedName("address_des") val address: String,
        @SerializedName("customer_name") val name: String,
        @SerializedName("province") private val _province: Region = Region(),
        @SerializedName("district") private val _district: Region = Region(),
        @SerializedName("phone_number") val phone: String,
        @SerializedName("address_type") val address_type: Int,
        @SerializedName("voucher_code") private val _voucher_code: String,
        @SerializedName("voucher_value") val voucher_value: Double,
        @SerializedName("total_pay") val totalPay: Double,
        @SerializedName("reason") val reason: String,
        @SerializedName("sub_orders") val sub_orders: ArrayList<SubOrder>,
        @SerializedName("pay_gateway") private val _pay_gateway: String,
        @SerializedName("created_at") private val _created_at: String


    ) : Parcelable {
        val pay_gateway
            get() = _pay_gateway.takeIf { _pay_gateway != null } ?: ""
        val voucher_code
            get() = _voucher_code.takeIf { _voucher_code != null } ?: ""
        val created_at
            get() = _created_at.takeIf { _created_at != null } ?: ""
        val province
            get() = _province.takeIf { _province != null } ?: Region()
        val district
            get() = _district.takeIf { _district != null } ?: Region()

        @Parcelize
        data class Item(
            @SerializedName("discount") val discount: Int,
            @SerializedName("product_uid") val itemUid: String,
            @SerializedName("display_name_detail") val displayNameDetail: String,
            @SerializedName("product_name") val productName: String,
            @SerializedName("quantity") val quantity: Int,
            @SerializedName("sell_price") val sell_price: Double,
            @SerializedName("square") val square: Boolean,
            @SerializedName("thumb") val thumb: String,
            @SerializedName("source") val source: String

        ) : Parcelable {
        }

        @Parcelize
        data class SubOrder(
            @SerializedName("order_id") val order_id: String,
            @SerializedName("order.partner") val partner: Partner?,
            @SerializedName("reason") val reason: String,
            @SerializedName("order_items") val items: ArrayList<Item>,

        ) : Parcelable {

            @Parcelize
            data class Partner(
                @SerializedName("id") val id: String,
                @SerializedName("partner_name") val partner_name: String?,
                @SerializedName("shipping_time") val shipping_time: Int

            ) : Parcelable {
            }
        }
    }
}