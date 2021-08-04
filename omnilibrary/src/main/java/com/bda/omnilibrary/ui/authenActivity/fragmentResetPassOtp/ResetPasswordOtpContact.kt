package com.bda.omnilibrary.ui.authenActivity.fragmentResetPassOtp

import com.bda.omnilibrary.model.LoginByPhoneRequest

class ResetPasswordOtpContact {
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