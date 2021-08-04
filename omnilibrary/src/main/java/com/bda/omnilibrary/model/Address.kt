package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("address_des") private var _address_des: String = "",
    @SerializedName("address_type") private var _address_type: Int = 1,
    @SerializedName("province") private var _customer_province: Region = Region(),
    @SerializedName("district") private var _customer_district: Region = Region()

) : Parcelable {
    var customer_province
        get() = _customer_province.takeIf { _customer_province != null } ?: Region()
        set(value) {
            _customer_province = value
        }

    var customer_district
        get() = _customer_district.takeIf { _customer_district != null } ?: Region()
        set(value) {
            _customer_district = value
        }

    var address_des
        get() = _address_des.takeIf { _address_des != null && _address_des.isNotEmpty() } ?: ""
        set(value) {
            _address_des = value
        }

    var address_type
        get() = _address_type.takeIf { _address_type != null } ?: 1
        set(value) {
            _address_type = value
        }
}