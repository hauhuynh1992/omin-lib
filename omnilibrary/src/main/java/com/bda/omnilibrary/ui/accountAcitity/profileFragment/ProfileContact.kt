package com.bda.omnilibrary.ui.accountAcitity.profileFragment

import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.CustomerProfileResponse

class ProfileContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(profile: CheckCustomerResponse)
        fun sendFalsed(message: String)
        fun showProgress()
        fun hideProgress()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun fetchProfile(uid: String)
        fun disposeAPI()
    }

}