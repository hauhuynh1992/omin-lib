package com.bda.omnilibrary.ui.invoiceDetailActivity

import com.bda.omnilibrary.model.*

class InvoiceDetailContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendListProductSuccessful(list: ArrayList<Product>)
        fun sendListProductFalse()

        fun cancelOrderSuccess()
        fun cancelOrderFailed()

    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun getListProductFromOrder(list: ArrayList<ListOrderResponceV3.Data.Item>)
        fun cancelOrderByOrder(orderId: String)

    }

}