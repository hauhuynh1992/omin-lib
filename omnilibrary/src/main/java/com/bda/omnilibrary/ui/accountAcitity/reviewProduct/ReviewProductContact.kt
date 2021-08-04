package com.bda.omnilibrary.ui.accountAcitity.reviewProduct

import com.bda.omnilibrary.model.ListOrderResponce

class ReviewProductContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(orders: ListOrderResponce)
        fun sendFalsed(message: Int)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter()
        fun disposeAPI()
    }

}