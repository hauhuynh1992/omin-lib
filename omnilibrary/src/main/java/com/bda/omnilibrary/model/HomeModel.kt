package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeModel(
    @SerializedName("statusCode") var statusCode: Int,
    @SerializedName("data") private var _data: Data?,
) : Parcelable {
    val data
        get() = _data.takeIf { _data != null }
            ?: Data()

    @Parcelize
    data class Data(
        @SerializedName("layout_name") private var _name: String? = "",
        @SerializedName("layout.layout_section") private var _LayoutSection: ArrayList<LayoutSection>?,
        @SerializedName("image_cover") var image_cover: String? = "",

        ) : Parcelable {
        constructor() : this(
            "", ArrayList(), ""
        )

        val name
            get() = _name.takeIf { _name != null }
                ?: ""
        val section
            get() = _LayoutSection.takeIf { _LayoutSection != null }
                ?: ArrayList()
    }

    @Parcelize
    data class LayoutSection(
        @SerializedName("uid") private var _uid: String,
        @SerializedName("section_value") private var _section_value: String,
        @SerializedName("section_name") private var _section_name: String,
        @SerializedName("section_ref") private var _section_ref: String,
        @SerializedName("section_type") private var _section_type: String,
        @SerializedName("layout_type") private var _layout_type: Int? = 0,
        @SerializedName("livestream") private var _liveStream: ArrayList<LiveStream>,
        @SerializedName("promotions") private var _promotions: ArrayList<Promotion>,
        @SerializedName("end_stream") private var _end_stream: ArrayList<LiveStream>,
        @SerializedName("collection") private var _collection: ArrayList<SimpleCollection>,
        @SerializedName("collection_temp") private var _collection_temp: ProductCollection,
        @SerializedName("viewed_prod") private var _viewed_prod: ArrayList<Product>,
        @SerializedName("favourite") private var _favourite: ArrayList<Product>,
        @SerializedName("buylater") private var _buylater: ArrayList<Product>,
        @SerializedName("brand_shop") private var _brand_shop: ArrayList<BrandShop>,
        @SerializedName("hybrid_section") private var _hybrid_section: ArrayList<HybridSection>,

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
        val layoutType
            get() = _layout_type.takeIf { _layout_type != null }
                ?: 1
        val promotions
            get() = _promotions.takeIf { _promotions != null } ?: ArrayList()
        val livestream
            get() = _liveStream.takeIf { _liveStream != null } ?: ArrayList()
        val endStream
            get() = _end_stream.takeIf { _end_stream != null } ?: ArrayList()
        val collections
            get() = _collection.takeIf { _collection != null }
                ?: ArrayList()
        val collectionTemp
            get() = _collection_temp.takeIf { _collection_temp != null }
                ?: ProductCollection()
        val viewed_prod
            get() = _viewed_prod.takeIf { _viewed_prod != null }
                ?: ArrayList()
        val favourite
            get() = _favourite.takeIf { _favourite != null }
                ?: ArrayList()
        val buylater
            get() = _buylater.takeIf { _buylater != null }
                ?: ArrayList()

        val brand_shop
            get() = _brand_shop.takeIf { _brand_shop != null }
                ?: ArrayList()

        val hybrid_section
            get() = _hybrid_section.takeIf { _hybrid_section != null }
                ?: ArrayList()
    }
}

