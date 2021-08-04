package com.bda.omnilibrary.ui.altAddressActivity

import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.DefaultAddressRequest
import com.bda.omnilibrary.model.DefaultAddressResponse

class AltAddressContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun sendSuccess(profile: CustomerProfileResponse)
        fun sendRemoveAltAddressSuccess(altId: String)
        fun sendFalsed(message: Int)
        fun sendDafaultAddressSuccess(response: DefaultAddressResponse)
        fun sendDafaultAddressFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun fetchDeliveryAddress(uid: String)
        fun removeAddress(uid: String, addresdId: String)
        fun postDefaultAddress(defaultAddressRequest: DefaultAddressRequest)
        fun disposeAPI()
    }

}