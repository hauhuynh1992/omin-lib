package com.bda.omnilibrary.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressSuggestResponce(
    @SerializedName("status") var status: String = "",
    @SerializedName("results") var results: ArrayList<Result>,
    @SerializedName("html_attributions") private var attributions: ArrayList<String>,
    @SerializedName("error_message") private var errorMessage: String,
    @SerializedName("next_page_token") private var nextPageToken: String

) : Parcelable {
    /*val customer_province
        get() = _customer_province.takeIf { _customer_province != null } ?: PlaceModel()*/

    @Parcelize
    data class Result(
        @SerializedName("formatted_address") var formattedAddress: String = "",
        @SerializedName("geometry") private var results: Geometry,
        @SerializedName("icon") private var _iconUrl: String,
        @SerializedName("id") private var _id: String,
        @SerializedName("name") private var _name: String,
        @SerializedName("place_id") private var _placeId: String,
        @SerializedName("reference") private var _reference: String,
        @SerializedName("vicinity") private var _vicinity: String
        //@SerializedName("opening_hours") private var _openingHours: OpeningHours,
        //@SerializedName("photos") private var _photos: ArrayList<Photo>

    ) : Parcelable {
        @Parcelize
        data class Geometry(
            @SerializedName("location") var location: Location
            //@SerializedName("viewport") var results: Viewport

        ) : Parcelable {
            @Parcelize
            data class Location(
                @SerializedName("lat") var lat: Double,
                @SerializedName("lng") var lng: Double
            ) : Parcelable {}
        }
    }
}