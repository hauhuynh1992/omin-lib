package com.bda.omnilibrary.ui.userInfomationActivity

import com.bda.omnilibrary.model.ProvinceDistrictModel

class LocationFirstContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendUISussess(model: ProvinceDistrictModel)
        fun sendUIFail(erroMessage: String)
        fun sendUIProvinceSussess(model: ProvinceDistrictModel)
        fun sendUIProvinceFail(erroMessage: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadDistrict(province_id: String)
        fun loadProvince()
        fun disposeAPI()
    }
}