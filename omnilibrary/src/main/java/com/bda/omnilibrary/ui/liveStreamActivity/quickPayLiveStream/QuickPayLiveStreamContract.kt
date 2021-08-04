package com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream

import com.bda.omnilibrary.model.CheckCustomerResponse

class QuickPayLiveStreamContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendAddressSuccess(data: CheckCustomerResponse)
        fun sendAddressFailed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun fetchProfile(userInfo: CheckCustomerResponse?)
    }
}