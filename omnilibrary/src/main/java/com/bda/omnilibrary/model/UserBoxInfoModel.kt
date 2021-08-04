package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserBoxInfoModel(
    @SerializedName("age_range") var ageRange: String,
    @SerializedName("vip_own") var vipOwn: ArrayList<String>,
    @SerializedName("account_type") var accountType: String,
    @SerializedName("user_phone") var userPhone: String,
    @SerializedName("paid_level") var paidLevel: Int,
    @SerializedName("user_avatar") var userAvatar: String,
    @SerializedName("over_limit") var overLimit: Int,
    @SerializedName("conv_required") var convRequired: Boolean,
    @SerializedName("sex_code") var sexCode: Int?,
    @SerializedName("user_id") var userId: String,
    @SerializedName("birthdate") var birthdate: String,
    @SerializedName("user_full_name") var userFullName: String,
    @SerializedName("apple_chos") var appleChos: Int,
    @SerializedName("location") var location: String,
    @SerializedName("need_active") var needActive: Boolean,
    @SerializedName("user_email") var userEmail: String,
    @SerializedName("dateleft") var dateleft: Int
) : Parcelable {

}