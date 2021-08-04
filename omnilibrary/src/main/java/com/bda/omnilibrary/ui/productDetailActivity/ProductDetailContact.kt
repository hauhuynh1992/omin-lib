package com.bda.omnilibrary.ui.productDetailActivity

import com.bda.omnilibrary.model.*

class ProductDetailContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSplashUIFail(erroMessage: String)
        fun sendSplashRelativeFail(erroMessage: String)
        fun sendSplashRelativeSuccess(list: ArrayList<Product>)
        fun sendProductSuccess(product: Product)
        fun sendProductFail()
        fun sendVoucher(voucher: Voucher)
        fun sendListPopUpFail(erroMessage: String)
        fun sendListPopUpSussess(model: PopUpResponse)
        fun sendActivePopUpFail(erroMessage: String)
        fun sendActivePopUpSussess(model: ActivePopUpResponse)
        fun sendLoadFavouriteSuccess(response: FavouriteResponse)
        fun sendAddressSuccess(data: CheckCustomerResponse)
        fun sendAddressFailed(message: String)
        fun finishThis(order_Id: String)
        fun sendFailedOrder(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun callApiRelative(idProduct: String)
        fun callApiProduct(uid: String)
        fun callVoucher(id: String, uid: String?)
        fun loadPopup(popUprequest: PopUpRequest)
        fun loadFavourite(customer_id: String, product_id: String)
        fun postAddFavourite(request: FavouriteResquest)
        fun postDeleteFavourite(request: FavouriteResquest)
        fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest)
        fun fetchProfile(userInfo: CheckCustomerResponse?)
        fun updateOrder(
            data: CheckCustomerResponse, product: Product,
            voucherCode: String, voucherId: String
        )
    }
}