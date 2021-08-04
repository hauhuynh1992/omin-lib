package com.bda.omnilibrary.ui.brandShopDetail

import com.bda.omnilibrary.model.*

class BrandShopDetailContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun getSuccess(brandShop: BrandShop, model: BrandShopModel)
        fun getHomeFailed(e: Int)
        fun getDetailFailed(e: Int)
        fun sendListPopUpFail(erroMessage: String)
        fun sendListPopUpSussess(model: PopUpResponse)
        fun sendProductSuccess(product: Product)
        fun sendActivePopUpFail(erroMessage: String)
        fun sendActivePopUpSussess(model: ActivePopUpResponse)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter(uid: String)
        fun disposeAPI()
        fun loadAddHistory(uid: String)
        fun loadPopup(popUprequest: PopUpRequest)
        fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest)
        fun callApiProduct(uid: String)
    }
}