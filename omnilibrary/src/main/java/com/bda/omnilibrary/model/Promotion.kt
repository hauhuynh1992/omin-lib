package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promotion(
    @SerializedName("uid") var uid: String = "",
    @SerializedName("display_name") var display_name: String = "",
    @SerializedName("display_name_detail") var display_name_detail: String = "",
    @SerializedName("from_date") var from_date: Long = 0,
    @SerializedName("to_date") var to_date: Long = 0,
    @SerializedName("description_html") var description_html: String = "",
    @SerializedName("image_banner") var image_banner: String = "",
    @SerializedName("image_cover") var image_cover: String = "",
    @SerializedName("promotion.button_direct") var button_direct: ButtonDirect? = null,
    @SerializedName("promotion.promotion_value") var promotion_value: PromotionValue? = null,
    @SerializedName("display_status") var display_status: Int = 0,
    @SerializedName("config_type") var config_type: Int = 0,
    @SerializedName("is_display") var is_display: Boolean = false,

    ) : Parcelable {

    @Parcelize
    data class ButtonDirect(
        @SerializedName("uid") var _uid: String = "",
        @SerializedName("link_name") var name: String = "",
        @SerializedName("link_direct") var direct: String = "",
        @SerializedName("link_value") var value: String = "",
        @SerializedName("link_action") var action: String = ""
    ) : Parcelable {
    }

    @Parcelize
    data class PromotionValue(
        @SerializedName("uid") var _uid: String = "",
        @SerializedName("promotion_name") var promotion_name: String = "",
        @SerializedName("promotion_type") var promotion_type: String = "",
        @SerializedName("promotion_values") var promotion_values: Double = 0.0,
        @SerializedName("condition_value") var condition_value: Double = 0.0,

        ) : Parcelable {
    }
}