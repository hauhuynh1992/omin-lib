package com.bda.omnilibrary

import com.bda.omnilibrary.util.Config

class OmniShop(
    private val mPlatForm: String,
    private val mXApiKey: String,
    private val mXApiKeyTracking: String,
    private val mToken: String
) {
    fun init() {
        LibConfig.xApiKey = mXApiKey
        LibConfig.xApiKey_tracking = mXApiKeyTracking
        LibConfig.token = mToken
        Config.platform = mPlatForm

    }
}