package com.bda.omnilibrary.ui.liveStreamActivity.paymentMethod

import com.bda.omnilibrary.model.Product

class PaymentMethodLiveContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(Oid: String)
        fun sendFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter(
            name: String,
            phone: String,
            vaucher: String,
            voucher_uid: String,
            requestType: Int,
            list: ArrayList<Product>? = null
        )

        fun disposeAPI()

    }
}