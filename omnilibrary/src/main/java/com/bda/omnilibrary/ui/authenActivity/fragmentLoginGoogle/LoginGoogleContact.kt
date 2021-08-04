package com.bda.omnilibrary.ui.authenActivity.fragmentLoginGoogle

class LoginGoogleContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showDeviceCode(user_code: String, url: String, device_code: String)
        fun showAssessToken(token: String, token_type: String)
        fun showGoogleDriveInfoToken(id: String, email: String, token: String, name: String)
        fun showError(error: String)
        fun showLoginByGoogleSuccess(customerId: String, customerName: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun getDeviceCode()
        fun getAccessToken(deviceCode: String)
        fun getAccessTokenInterval(deviceCode: String)
        fun getDriveInfo(token: String, token_type: String)
        fun loginWithGoogle(id: String, email: String, token: String, name: String)
        fun disposeAPI()
    }
}