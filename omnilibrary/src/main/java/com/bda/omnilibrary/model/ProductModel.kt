package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    @SerializedName("uid") private var _uid: String,
    @SerializedName("product_uid") private var product_uid: String,
    @SerializedName("sku_id") var sku_id: String,
    @SerializedName("product_name") private var _product_name: String,
    @SerializedName("display_name") private var _display_name: String,
    @SerializedName("display_name_detail") private var _display_name_detail: String,
    @SerializedName("color") var color: String,
    @SerializedName("instock") var instock: Boolean,
    @SerializedName("show_detail") var show_detail: Boolean,
    @SerializedName("promotion_desc") private var _promotion_desc: String,
    @SerializedName("short_desc") var _short_desc: String,
    @SerializedName("product.pricing") private var pricing: Pricing,
    @SerializedName("product.collection") private var _collection: ArrayList<Collection>,
    @SerializedName("product.brand") private var _brand: Brand,
    @SerializedName("images") private var _images: ArrayList<MediaType>,
    @SerializedName("videos") private var _videos: ArrayList<MediaType>,
    @SerializedName("video_transcodes") private var _video_transcodes: ArrayList<MediaType>,
    @SerializedName("image_cover") private var _imageCover: String,
    @SerializedName("image_highlight") private var _imageHighlight: String,
    @SerializedName("image_banner") private var _imageBanner: String,
    @SerializedName("original") private var _original: String,
    @SerializedName("quantity") var order_quantity: Int = 1,
    @SerializedName("animation") var animation: Boolean = false,
    @SerializedName("product.manufacturer") private var _manufacturer: Manufacturer,
    @SerializedName("product.supplier") private var _supplier: Supplier,
    @SerializedName("detail") var detail: Detail,
    @SerializedName("sell_price") var sell_price: Double = 0.0,
    @SerializedName("viewcount") var viewcount: Int = 1,
    @SerializedName("autoplay_video") var autoplay_video: Boolean = false,
    @SerializedName("trailer") private var _trailer: String = "",
    @SerializedName("ribbon") private var _ribbon: String,
    @SerializedName("discount_tag") private var _discount_tag: String,
    @SerializedName("free_shipping_tag") var free_shipping_tag: Boolean,
    @SerializedName("areas") private var _areas: ArrayList<Region>,
    @SerializedName("keywords") private var _keywords: String,
    @SerializedName("display_bill_name") var display_bill_name: String = "",

    @SerializedName("product.tag") private var _tags: ArrayList<Tag>,
    @SerializedName("is_disabled_cod") private var _is_disabled_cod: Boolean,
    @SerializedName("short_descriptions") private var _short_descriptions: ShortDescription,

    @SerializedName("is_disabled_quickpay") var is_disabled_quickpay: Boolean = false,
    @SerializedName("product.specs") var specs: ArrayList<Spec>? = null,
    @SerializedName("description_html") var description_html: String? = null,
    @SerializedName("brand_shop") var brand_shop: ArrayList<BrandShop>? = null,
    @SerializedName("is_supplier_condition_enable") var isDisableBySupplierCondition: Boolean = false,

    @SerializedName("is_first_supplier") var is_first_supplier: Boolean = false
    ) : Parcelable {

    constructor(animation: Boolean) : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        true,
        true,
        "",
        "",
        Pricing(),
        ArrayList<Collection>(),
        Brand(),
        ArrayList<MediaType>(),
        ArrayList<MediaType>(),
        ArrayList<MediaType>(),
        "",
        "", "", "",
        1,
        animation,
        Manufacturer(),
        Supplier(),
        Detail(),
        0.0,
        1, false, "",
        "",
        "",
        false,
        ArrayList(), "", "",


        ArrayList(),
        false,
        ShortDescription(), false
    )

    val name
        get() = (_display_name_detail.takeIf { _display_name_detail != null && _display_name_detail.isNotEmpty() }
            ?: _product_name.takeIf { _product_name != null && _product_name.isNotEmpty() }
            ?: "").trim()

    val keywords
        get() = (_keywords.takeIf { _keywords != null && _keywords.isNotEmpty() }
            ?: _keywords).trim()

    val display_name_detail
        get() = (_display_name_detail.takeIf { _display_name_detail != null && _display_name_detail.isNotEmpty() }
            ?: name).trim()

    val price
        get() = (pricing.takeIf { pricing != null }
            ?: Pricing()).price_with_vat.takeIf {
            (pricing.takeIf { pricing != null }
                ?: Pricing()).price_with_vat > 0
        } ?: sell_price
    val listedPrice
        get() = (pricing.takeIf { pricing != null }
            ?: Pricing()).listed_price_with_vat
    val supplier
        get() = (_supplier.takeIf { _supplier != null }
            ?: Supplier())
    val collection
        get() = (_collection.takeIf { _collection != null }
            ?: ArrayList())
    val images
        get() = (_images.takeIf { _images != null }
            ?: ArrayList())
    val video_transcodes
        get() = (_video_transcodes.takeIf { _video_transcodes != null }
            ?: ArrayList())
    val videos_temp
        get() = (_videos.takeIf { _videos != null }
            ?: ArrayList())
    val videos
        get() = video_transcodes + videos_temp

    val brand
        get() = (_brand.takeIf { _brand != null }
            ?: Brand())
    val manufacturer
        get() = (_manufacturer.takeIf { _manufacturer != null }
            ?: Manufacturer())
    val uid
        get() = _uid.takeIf { _uid != null && _uid.isNotEmpty() } ?: product_uid
    val imageCover
        get() = _imageCover.takeIf { _imageCover != null && _imageCover.isNotEmpty() } ?: ""
    val original
        get() = _original.takeIf { _original != null && _original.isNotEmpty() } ?: ""
    val short_desc
        get() = (_short_desc.takeIf { _short_desc != null && _short_desc.isNotEmpty() }
            ?: "").trim()
    val promotion_desc
        get() = (_promotion_desc.takeIf { _promotion_desc != null && _promotion_desc.isNotEmpty() }
            ?: "").trim()
    val imageHighlight
        get() = _imageHighlight.takeIf { _imageHighlight != null && _imageHighlight.isNotEmpty() }
            ?: ""
    val imageBanner
        get() = _imageBanner.takeIf { _imageBanner != null && _imageBanner.isNotEmpty() }
            ?: ""

    val trailer
        get() = _trailer.takeIf { _trailer != null && _trailer.isNotEmpty() }
            ?: ""
    val discount_tag
        get() = (_discount_tag.takeIf { _discount_tag != null && _discount_tag.isNotEmpty() }
            ?: "").trim()
    val ribbon
        get() = (_ribbon.takeIf { _ribbon != null && _ribbon.isNotEmpty() }
            ?: "").trim()
    val areas
        get() = _areas.takeIf { _areas != null }
            ?: ArrayList()

    val tags
        get() = _tags.takeIf { _tags != null }
            ?: ArrayList()
    val short_descriptions
        get() = _short_descriptions.takeIf { _short_descriptions != null }
            ?: ShortDescription()

    val is_disabled_cod
        get() = _is_disabled_cod.takeIf { _is_disabled_cod != null }
            ?: false

    @Parcelize
    data class Brand(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("brand_name") var name: String = "",
    ) : Parcelable {

    }

    @Parcelize
    data class ShortDescription(
        @SerializedName("short_des_1") var short_des_1: String = "",
        @SerializedName("short_des_1_color") var short_des_1_color: String = "",
        @SerializedName("short_des_2") var short_des_2: String = "",
        @SerializedName("short_des_2_color") var short_des_2_color: String = "",
        @SerializedName("short_des_3") var short_des_3: String = "",
        @SerializedName("short_des_3_color") var short_des_3_color: String = "",
        @SerializedName("short_des_4") var short_des_4: String = "",
        @SerializedName("short_des_4_color") var short_des_4_color: String = ""
    ) : Parcelable {

    }

    @Parcelize
    data class Supplier(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("supplier_name") var supplier_name: String = "đang cập nhật",
        @SerializedName("required_order_value") var required_order_value: Double = 0.0,
        @SerializedName("supplier_id") var supplier_id: String = "",
        @SerializedName("shipping_time") var shipping_time: Int = -1,
    ) : Parcelable {

    }

    @Parcelize
    data class Collection(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("collection_name") var collection_name: String = "",
    ) : Parcelable {

    }

    @Parcelize
    data class Manufacturer(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("manufacturer_name") var manufacturer_name: String = "",
    ) : Parcelable {

    }

    @Parcelize
    data class Pricing(
        @SerializedName("price_with_vat") var price_with_vat: Double = 0.0,
        @SerializedName("listed_price_with_vat") var listed_price_with_vat: Double = 0.0,
        @SerializedName("shipping_fee") var shipping_fee: Double = 0.0,
    ) : Parcelable {

    }


    @Parcelize
    data class MediaType(
        @SerializedName("media_type") var mediaType: String = "",
        @SerializedName("thumb") var icon: String = "",
        @SerializedName("source") var url: String = "",
        @SerializedName("square") var square: Boolean,

        ) : Parcelable {
    }

    @Parcelize
    data class Detail(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("content") var content: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("icon") var icon: String = "",
    ) : Parcelable {
    }

    @Parcelize
    data class Tag(
        @SerializedName("uid") var uid: String = "",
        @SerializedName("tag_title") var tag_title: String = "",
        @SerializedName("tag_category") var tag_category: String = "",
        @SerializedName("tag_type") var tag_type: String = "",
        @SerializedName("percentage") var percentage: String = "",
        @SerializedName("fee") var fee: String = "",
        @SerializedName("display_status") var display_status: Int,
        @SerializedName("image_promotion") var image_promotion: String = "",
        @SerializedName("name_tag_value_1") var name_tag_value_1: String = "",
        @SerializedName("name_tag_value_2") var name_tag_value_2: String = "",
    ) : Parcelable {
    }

    @Parcelize
    data class Description(
        @SerializedName("time") var time: Double = 0.0,
        @SerializedName("blocks") var blocks: ArrayList<Block>,
        @SerializedName("version") var version: String = "",
    ) : Parcelable {
        constructor() : this(0.0, arrayListOf())
    }

    @Parcelize
    data class Block(
        @SerializedName("type") var type: String = "",
        @SerializedName("data") var data: Data,
    ) : Parcelable {
        @Parcelize
        data class Data(
            @SerializedName("text") var text: String = "",
            @SerializedName("level") var level: Int = -1,
        ) : Parcelable {
        }
    }

    @Parcelize
    data class Spec(
        @SerializedName("uid") var uid: String? = null,
        @SerializedName("spec_key") var spec_key: String? = null,
        @SerializedName("spec_value") var spec_value: String? = null,
    ) : Parcelable {
    }

}