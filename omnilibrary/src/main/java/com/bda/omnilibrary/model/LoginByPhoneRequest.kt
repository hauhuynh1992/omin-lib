package com.bda.omnilibrary.model

import android.os.Parcelable
import com.bda.omnilibrary.util.Duplex
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginByPhoneRequest(
    @SerializedName("phone_number") var phoneNumber: String,
    @SerializedName("password") var password: String
) : Parcelable {

    fun validate(): Duplex<Boolean, String?> {
        phoneNumber.trim().takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, "Bạn chưa nhập số điện thoại")
        password.takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, "Bạn chưa nhập mật khẩu")
        return Duplex(true, null)
    }

}

