package com.bda.omnilibrary.ui.accountAcitity.editProfileFragment

import com.bda.omnilibrary.model.UpdateCustomerProfileRequest

class EditProfileContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess()
        fun sendFalsed(message: String)
        fun showProgress()
        fun hideProgress()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun updateCustomProfile(request: UpdateCustomerProfileRequest)
        fun disposeAPI()
    }
}