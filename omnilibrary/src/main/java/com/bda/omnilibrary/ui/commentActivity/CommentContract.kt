package com.bda.omnilibrary.ui.commentActivity

import com.bda.omnilibrary.model.CheckCustomerResponse

class CommentContract {

    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendQrCodeBase64(encode: String)
        fun sendSuccess()
        fun sendFalsed(message: String)
        fun finishActivity()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter(addressData: CheckCustomerResponse,vaucher:String,voucher_uid:String)
        fun cancelOrder()
        fun disposeAPI()
    }

}