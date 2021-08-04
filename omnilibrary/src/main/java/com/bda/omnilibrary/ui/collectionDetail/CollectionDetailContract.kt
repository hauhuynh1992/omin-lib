package com.bda.omnilibrary.ui.collectionDetail

import com.bda.omnilibrary.model.*

class CollectionDetailContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendFalsed(message: String)
        fun sendSuccessProducts(model:ChildDetails)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun getCollectionProduct(uid: String, type: String, length: Int, page: Int)
        fun loadAddHistory(uid: String)
        fun loadAddHistoryBrandShop(uid: String, brandId: String)
        fun disposeAPI()
        fun getBrandShopChildCollection(uid: String, type: String, length: Int, page: Int)
    }
}