package com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment

import com.bda.omnilibrary.model.LoginByPhoneRequest

class ResetPasswordPhoneContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun showInputPhoneSuccess(customerId: String, customerName: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun resetPasswordInputPhone(loginRequest: LoginByPhoneRequest)
        fun disposeAPI()
    }
}