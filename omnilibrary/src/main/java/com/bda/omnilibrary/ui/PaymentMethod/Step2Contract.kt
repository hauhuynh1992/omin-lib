package com.bda.omnilibrary.ui.PaymentMethod

import com.bda.omnilibrary.model.Product

class Step2Contract {
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
            vaucher: String,
            voucher_uid: String,
            requestType: Int,
            list: ArrayList<Product>? = null
        )

        fun disposeAPI()

    }
}