package com.bda.omnilibrary.ui.authenActivity.fragmentSignupOtpPhone

import com.bda.omnilibrary.model.RegisterOtpRequest

class SignUpOtpContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun showSignUpNamePhoneSuccess(customerId: String, customerName: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun signUpOtp(loginRequest: RegisterOtpRequest)
        fun disposeAPI()
    }
}