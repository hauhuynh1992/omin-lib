package com.bda.omnilibrary.ui.accountAcitity.orderFragment

import com.bda.omnilibrary.model.ListOrderResponceV3

class OrderContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(orders: ArrayList<ListOrderResponceV3.Data>)
        fun sendFalsed(message: Int)
        fun showLoading()
        fun hideLoading()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun getOrderByStatus(status: String, page: Int)
        fun disposeAPI()
    }

}