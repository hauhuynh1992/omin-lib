package com.bda.omnilibrary.dialog.address

import com.bda.omnilibrary.model.ProvinceDistrictModel

class LocationFirstContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendUIFail(erroMessage: String)
        fun sendUIProvinceSussess(model: ProvinceDistrictModel)
        fun sendUIDistrictSussess(model: ProvinceDistrictModel)
        fun sendUILocationSuccess(address: String)
        fun requestPermission()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadDistrict(province_id: String)
        fun loadProvince()
        fun loadLocation()
        fun disposeAPI()
    }
}