package com.bda.omnilibrary.ui.categoryDetail

import com.bda.omnilibrary.model.*

class CategoryDetailContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendFalsed(message: String)
        fun sendCollectionsHomeSuccess(subCollection: SubCollection)
        fun sendProductsSuccess(model: ProductMoreModel)
        fun sendProductSuccess(product: Product)
        fun sendListPopUpFail(erroMessage: String)
        fun sendListPopUpSussess(model: PopUpResponse)
        fun sendActivePopUpFail(erroMessage: String)
        fun sendActivePopUpSussess(model: ActivePopUpResponse)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadProductMore(productMoreRequestModel: ProductMoreRequestModel)
        fun getSubCollectionHome(uid: String)
        fun loadAddHistory(uid: String)
        fun loadAddHistoryBrandShop(uid: String, brandId: String)
        fun disposeAPI()
        fun getSubCollectionBrandShop(brandId: String, collectionId: String)
        fun loadProductMoreBrandShop(productMoreRequestModel: ProductBrandShopMoreRequestModel)
        fun loadPopup(popUprequest: PopUpRequest)
        fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest)
        fun callApiProduct(uid: String)
        fun getNewArrivalProducts(uid: String, page: Int, length: Int)
    }
}