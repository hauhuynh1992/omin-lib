package com.bda.omnilibrary.ui.accountAcitity.editDeliveryAddressFragment

import com.bda.omnilibrary.model.DefaultAddressRequest
import com.bda.omnilibrary.model.UpdateCustomerAltRequest

class EditDeliveryAddressContact {
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
        fun updateCustomAltInfo(request: UpdateCustomerAltRequest)
        fun postDefaultAddress(defaultAddressRequest: DefaultAddressRequest)
        fun addCustomAltInfo(request: UpdateCustomerAltRequest)
        fun removeAddress(uid: String, addresdId: String)
        fun disposeAPI()
    }

}