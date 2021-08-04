package com.bda.omnilibrary.ui.liveStreamActivity

import com.bda.omnilibrary.model.*

class LiveStreamContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(liveStream: LiveStream)
        fun sendFalsed(error: String)
        fun sendAddressSuccess(data: CheckCustomerResponse)
        fun sendAddressFailed(message: String)
        fun finishThis(order_Id: String)
        fun sendFailedOrder(message: String)
        fun seekToProductTime(time: Int)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter(liveStreamID: String)
        fun disposeAPI()
        fun postAddFavourite(request: FavouriteResquest)
        fun postDeleteFavourite(request: FavouriteResquest)
        fun fetchProfile(userInfo: CheckCustomerResponse?)
        fun updateOrder(
            data: CheckCustomerResponse, product: Product,
            voucherCode: String, voucherId: String
        )
    }
}