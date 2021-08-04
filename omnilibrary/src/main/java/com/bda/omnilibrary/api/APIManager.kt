package com.bda.omnilibrary.api

import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.util.Config

class APIManager {
    private val HOST_OMNISHOP_EU_URL = LibConfig.baseEuUrl
    private val HOST_URL = LibConfig.devBaseUrl

    private val mApi: ItemAPI
    private val generator: APIGenerator =
        if (LibConfig.APP_ORIGIN == Config.PARTNER.OMNISHOPEU.toString()) {
            APIGenerator(HOST_OMNISHOP_EU_URL)
        } else {
            APIGenerator(HOST_URL)
        }

    init {

        mApi = generator.createService(ItemAPI::class.java)
    }

    fun getApi(): ItemAPI {
        return mApi
    }

    companion object {
        private var sInstance: APIManager? = null

        @Synchronized
        fun getInstance(): APIManager {
            if (sInstance == null) {
                sInstance = APIManager()
            }
            return sInstance as APIManager
        }
    }

}