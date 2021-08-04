package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class HybridSection(
    @SerializedName("uid") private var _uid: String?,
    @SerializedName("section_value") private var _section_value: String?,
    @SerializedName("section_name") private var _section_name: String?,
    @SerializedName("section_ref") private var _section_ref: String?,
    @SerializedName("section_type") private var _section_type: String?,
    @SerializedName("image")  var image: String?,
    @SerializedName("image_background")  var image_background: String?
) : Parcelable {

    val uid
        get() = _uid.takeIf { _uid != null }
            ?: ""
    val sectionValue
        get() = _section_value.takeIf { _section_value != null }
            ?: ""
    val sectionName
        get() = _section_name.takeIf { _section_name != null }
            ?: ""
    val sectionRef
        get() = _section_ref.takeIf { _section_ref != null }
            ?: ""
    val sectionType
        get() = _section_type.takeIf { _section_type != null }
            ?: ""
}