package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckCustomerResponse(
    @SerializedName("statusCode") var status: Int = 1,
    @SerializedName("data") var data: Data = Data(
        "",
        "",
        "",
        "",
        "",
        "",
        Address(),
        0,
        "",
        "",
        ArrayList()
    )
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("session_id") var session_id: String = "",
        @SerializedName("fptplay_id") var fptplay_id: String = "",
        @SerializedName("phone_number") private var _phone: String = "",    // lầy từ BOX, không được sửa
        @SerializedName("profile_phone_number") private var _profile_phone_number: String = "", // User nhập, có thể sửa
        @SerializedName("customer_name") private var _name: String = "",
        @SerializedName("address") private var _address: Address = Address(),
        @SerializedName("gender") private var _gender: Int,
        @SerializedName("dateOfBirth") private var _dateOfBirth: String = "",
        @SerializedName("email") private var _email: String = "",
        @SerializedName("alt_info") private var _alt_info: ArrayList<ContactInfo>

    ) : Parcelable {

        var name
            get() = _name.takeIf { _name != null && _name.isNotEmpty() } ?: ""
            set(value) {
                _name = value
            }

        var gender
            get() = _gender.takeIf { _gender != null } ?: 0
            set(value) {
                _gender = value
            }

        var email
            get() = _email.takeIf { _email != null && _email.isNotEmpty() } ?: ""
            set(value) {
                _email = value
            }

        var dateOfBirth
            get() = _dateOfBirth.takeIf { _dateOfBirth != null && _dateOfBirth.isNotEmpty() } ?: ""
            set(value) {
                _dateOfBirth = value
            }

        var customer_phone
            get() = _profile_phone_number.takeIf { _profile_phone_number != null && _profile_phone_number.isNotEmpty() }
                ?: _phone.takeIf { _phone!=null }?:""
            set(value) {
                _profile_phone_number = value
            }

        var phone
            get() = _phone.takeIf { _phone != null && _phone.isNotEmpty() } ?: _profile_phone_number.takeIf { _profile_phone_number!=null }?:""
            set(value) {
                _phone = value
            }

        var address
            get() = _address.takeIf { _address != null } ?: Address()
            set(value) {
                _address = value
            }
        var alt_info
            get() = _alt_info.takeIf { _alt_info != null } ?: ArrayList()
            set(value) {
                _alt_info = value
            }
    }
}