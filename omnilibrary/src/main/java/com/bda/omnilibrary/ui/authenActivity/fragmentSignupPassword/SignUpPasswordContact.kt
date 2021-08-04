package com.bda.omnilibrary.ui.authenActivity.fragmentSignupPassword

import com.bda.omnilibrary.model.RegisterOtpRequest
import com.bda.omnilibrary.model.RegisterPasswordRequest

class SignUpPasswordContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun showSignUpPasswordSuccess(customerId: String, customerName: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun signUpPassword(loginRequest: RegisterPasswordRequest)
        fun disposeAPI()
    }
}