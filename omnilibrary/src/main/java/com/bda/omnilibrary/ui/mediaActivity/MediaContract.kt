package com.bda.omnilibrary.ui.mediaActivity

import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.Product

class MediaContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
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
        fun fetchProfile(userInfo: CheckCustomerResponse?)
        fun updateOrder(
            data: CheckCustomerResponse, product: Product,
            voucherCode: String, voucherId: String
        )
    }
}