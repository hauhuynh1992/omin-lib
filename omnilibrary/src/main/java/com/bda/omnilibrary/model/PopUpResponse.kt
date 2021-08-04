package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PopUpResponse(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("result") var result: ArrayList<ResultPopup>
) : Parcelable {
    @Parcelize
    data class ResultPopup(
        @SerializedName("uid") var uid: String,
        @SerializedName("popup_image") var popup_image: String,
        @SerializedName("popup_type") var popup_type: String,
        @SerializedName("popup_product") var popup_product: Popup,
        @SerializedName("popup_collection") var popup_collection: Popup,
        @SerializedName("popup_collection_temp") var popup_collection_temp: Popup,
        @SerializedName("popup_brandshop") var popup_brandshop: Popup,
        @SerializedName("popup_voucher") var popup_voucher: Popup,
        @SerializedName("landing_page") var landing_page: Popup
    ) : Parcelable {
        @Parcelize
        data class Popup(
            @SerializedName("uid") var uid: String,
            @SerializedName("name") private var _name: String,
            @SerializedName("collection_name") private var collection_name: String,
            @SerializedName("brand_shop_name") var brand_shop_name: String,
            @SerializedName("landing_name") var landing_name: String,
            @SerializedName("image_cover") var image_cover: String,
            @SerializedName("parent") var parent: Parent

        ) : Parcelable {
            val name
                get() = (_name.takeIf { _name != null && _name.isNotEmpty() }
                    ?: collection_name.takeIf { collection_name != null && collection_name.isNotEmpty() }
                    ?: "").trim()

            @Parcelize
            data class Parent(
                @SerializedName("uid") var uid: String,
                @SerializedName("collection_name") var collection_name: String
            ) : Parcelable {

            }
        }

    }
}