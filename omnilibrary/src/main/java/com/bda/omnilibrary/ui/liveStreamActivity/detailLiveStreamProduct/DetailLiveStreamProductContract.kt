package com.bda.omnilibrary.ui.liveStreamActivity.detailLiveStreamProduct

import com.bda.omnilibrary.model.FavouriteResponse
import com.bda.omnilibrary.model.FavouriteResquest

class DetailLiveStreamProductContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendLoadFavouriteSuccess(response: FavouriteResponse)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun loadFavourite(customer_id: String, product_id: String)
        fun postDeleteFavourite(request: FavouriteResquest)
    }
}