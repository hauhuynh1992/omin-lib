package com.bda.omnilibrary.api

import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.util.Config

class APIManager {
    private val HOST_URL = LibConfig.proBaseUrl

    private val mApi: ItemAPI
    private val generator: APIGenerator = APIGenerator(HOST_URL)

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