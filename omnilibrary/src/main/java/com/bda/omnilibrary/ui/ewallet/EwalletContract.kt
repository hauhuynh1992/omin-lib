package com.bda.omnilibrary.ui.ewallet

import com.bda.omnilibrary.model.Product

class EwalletContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendQrCodeBase64(encode: String)
        fun sendSuccess(oid: String)
        fun sendFalsed(message: String)
        fun finishActivity()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter(vaucher: String, voucher_uid: String, list: ArrayList<Product>?)
        fun disposeAPI()
    }
}