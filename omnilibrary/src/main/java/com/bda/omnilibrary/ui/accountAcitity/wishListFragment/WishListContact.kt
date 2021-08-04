package com.bda.omnilibrary.ui.accountAcitity.wishListFragment

import com.bda.omnilibrary.model.Product

class WishListContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(products: ArrayList<Product>)
        fun sendRemoveFavouriteSuccess(id: String)
        fun sendFalsed(message: Int)
        fun showProgress()
        fun hideProgress()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun getListFavourite(page: Int)
        fun getRemoveFavourite(productId: String)
        fun disposeAPI()
    }

}