package com.bda.omnilibrary.ui.userInfomationActivity

import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.Product

class UserInformationContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun loadFragment()

        fun sendUpdateCustomer(updateCustomerResponce: CheckCustomerResponse)
        fun sendUpdateCustomerFail(error: String)
        fun sendLocationSuccess(address: String)
        fun sendError(error: String)
        fun requestPermission()

        fun sendProfileSuccess(defaultName: String, defaultPhone: String)
        fun sendProfileFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadPresenter()
        fun loadLocation()
        fun updateAddress(userInfoTemp: CheckCustomerResponse)
        fun disposeAPI()
        fun fetchProfile()
    }
}