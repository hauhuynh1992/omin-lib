package com.bda.omnilibrary.ui.authenActivity.fragmentLoginPhone

import com.bda.omnilibrary.model.LoginByPhoneRequest

class LoginPhoneContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun showLoginByPhoneSuccess(customerId: String, customerName: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loginWithPhone(loginRequest: LoginByPhoneRequest)
        fun disposeAPI()
    }
}