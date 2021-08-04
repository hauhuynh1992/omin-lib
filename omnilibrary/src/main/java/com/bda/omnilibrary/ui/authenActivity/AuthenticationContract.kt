package com.bda.omnilibrary.ui.authenActivity

import com.bda.omnilibrary.model.CheckCustomerResponse

class AuthenticationContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(
            checkCustomerResponse: CheckCustomerResponse
        )

        fun sendError(error: String)

    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadCustomer(uid: String)
        fun disposeAPI()
    }
}