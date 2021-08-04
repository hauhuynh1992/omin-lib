package com.bda.omnilibrary.ui.authenActivity.fragmentSignupNamePhone

import com.bda.omnilibrary.model.LoginByPhoneRequest
import com.bda.omnilibrary.model.RegisterPhoneNameRequest

class SignUpNamePhoneContact {
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
        fun signUpNamePhone(loginRequest: RegisterPhoneNameRequest)
        fun disposeAPI()
    }
}