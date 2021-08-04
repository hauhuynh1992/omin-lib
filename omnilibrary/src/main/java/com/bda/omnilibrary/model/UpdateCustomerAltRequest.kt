package com.bda.omnilibrary.model

import android.content.Context
import android.os.Parcelable
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.Duplex
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateCustomerAltRequest(
    @SerializedName("uid") var uid: String? = null,
    @SerializedName("alt_info") var alt_info: ContactInfo? = null
) : Parcelable {

    fun validate(context:Context): Duplex<Boolean, String?> {
        alt_info?.customer_name!!.trim().takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, context.getString(R.string.no_reciever_name))
        alt_info?.phone_number!!.takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, context.getString(R.string.phone_require))
        alt_info?.address!!.address_des.takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, context.getString(R.string.no_reciever_address))
        alt_info?.address!!.customer_district.uid.takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false,context.getString(R.string.text_ban_chua_chon_quan_huyen))
        alt_info?.address!!.customer_province.uid.takeUnless { it.isNullOrEmpty() }
            ?: return Duplex(false, context.getString(R.string.text_ban_chua_chon_tinh_thanh_pho))
        return Duplex(true, null)
    }
}