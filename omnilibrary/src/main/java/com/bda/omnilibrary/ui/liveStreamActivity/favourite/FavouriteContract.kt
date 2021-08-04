package com.bda.omnilibrary.ui.liveStreamActivity.favourite

import com.bda.omnilibrary.model.FavouriteResquest
import com.bda.omnilibrary.model.Product

class FavouriteContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendFalsed(message: String)
        fun showProgress()
        fun hideProgress()
        fun sendListFavouriteSuccess(products: ArrayList<Product>)
        fun sendAddFavouriteSuccess()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun postAddFavourite(productID: String)
        fun getListFavourite(page: Int)
        fun disposeAPI()
    }
}