package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerProfileResponse(
    @SerializedName("uid") var uid: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("customer_name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone_number") var phone: String? = null,
    @SerializedName("profile_phone_number") var profile_phone_number: String? = null,
    @SerializedName("dateOfBirth") var birth_day: Long? = 0,
    @SerializedName("alt_info") var alt_info: ArrayList<ContactInfo>? = null,
    @SerializedName("address") var address: Address? = null
) : Parcelable {

    @Parcelize
    data class Address(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("address_des") var address_des: String? = null,
        @SerializedName("customer_name") var name: String? = null,
        @SerializedName("address_type") var address_type: Int? = 1,
        @SerializedName("district") var district: Local? = null,
        @SerializedName("province") var province: Local? = null
    ) : Parcelable {

        @Parcelize
        data class Local(
            @SerializedName("uid") var uid: String? = null,
            @SerializedName("name") var name: String? = null
        ) : Parcelable {
        }
    }

}
